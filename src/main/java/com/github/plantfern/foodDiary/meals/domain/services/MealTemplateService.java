package com.github.plantfern.foodDiary.meals.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.food.api.dto.ProductServingDto;
import com.github.plantfern.foodDiary.meals.api.dto.MealTemplateDto;
import com.github.plantfern.foodDiary.meals.api.dto.MealTemplateFoodRecordItemDto;
import com.github.plantfern.foodDiary.meals.api.dto.MealTemplateListItemDto;
import com.github.plantfern.foodDiary.meals.domain.MealPolicy;
import com.github.plantfern.foodDiary.meals.domain.entities.MealTemplateEntity;
import com.github.plantfern.foodDiary.meals.domain.entities.MealTemplateFoodRecordEntity;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealTemplateFoodRecordRepository;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealTemplateRepository;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MealTemplateService {

    private final MealTemplateRepository mealTemplateRepository;
    private final MealTemplateFoodRecordRepository mealTemplateFoodRecordRepository;
    private final DiaryProfileApi diaryProfileApi;
    private final MealPolicy mealPolicy;
    private final CurrentUser currentUser;
    private final FoodServingApi foodServingApi;

    @Transactional(readOnly = true)
    public List<MealTemplateListItemDto> listByDiaryProfile(Long diaryProfileId) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureIsOwner(currentUser, ownerId);
        return mealTemplateRepository.findAllByDiaryProfileId(diaryProfileId).stream()
                .map(t -> new MealTemplateListItemDto(
                        t.getId(),
                        t.getName(),
                        t.getScheduledTime(),
                        t.getFrequency(),
                        null,
                        null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public MealTemplateDto getDetail(Long templateId) {
        var template = getById(templateId);
        ensureCanWrite(template);

        var rows = mealTemplateFoodRecordRepository.findAllByMealTemplateId(templateId);
        var servingIds = rows.stream()
                .map(MealTemplateFoodRecordEntity::getServingId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, ProductServingDto> servingMap = servingIds.isEmpty()
                ? Map.of()
                : foodServingApi.getInfoByServingIds(servingIds, 1L);

        List<MealTemplateFoodRecordItemDto> records = rows.stream()
                .map(r -> toRecordItem(r, servingMap.get(r.getServingId())))
                .toList();

        return new MealTemplateDto(
                template.getId(),
                template.getDiaryProfileId(),
                template.getName(),
                template.getScheduledTime(),
                template.getFrequency(),
                template.getStartedAt(),
                records
        );
    }

    private MealTemplateFoodRecordItemDto toRecordItem(
            MealTemplateFoodRecordEntity r,
            ProductServingDto serving
    ) {
        Float nutrient = null;
        if (serving != null
                && serving.gramWeight() != null
                && serving.nutrientPer100g() != null
                && r.getAmount() != null) {
            float servingAmount = serving.servingAmount() != null ? serving.servingAmount() : 1f;
            nutrient = (r.getAmount() * serving.gramWeight() * servingAmount / 100f)
                    * serving.nutrientPer100g();
        }

        return new MealTemplateFoodRecordItemDto(
                r.getId(),
                r.getServingId(),
                serving != null ? serving.productDescription() : null,
                r.getAmount(),
                serving != null ? serving.servingAmount() : null,
                serving != null ? serving.gramWeight() : null,
                serving != null ? serving.servingUnitCode() : null,
                nutrient
        );
    }

    @Transactional
    public Long create(
            Long diaryProfileId,
            String name,
            LocalTime scheduledTime,
            Long frequency,
            LocalDate startedAt
    ) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        var entity = new MealTemplateEntity(
                diaryProfileId,
                name,
                scheduledTime,
                frequency,
                startedAt
        );
        return mealTemplateRepository.save(entity).getId();
    }

    @Transactional
    public void update(
            Long templateId,
            String name,
            LocalTime scheduledTime,
            Long frequency,
            LocalDate startedAt
    ) {
        var template = getById(templateId);
        ensureCanWrite(template);

        template.setName(name);
        template.setScheduledTime(scheduledTime);
        template.setFrequency(frequency);
        template.setStartedAt(startedAt);
        mealTemplateRepository.save(template);
    }

    @Transactional
    public void softDelete(Long templateId) {
        var template = getById(templateId);
        ensureCanWrite(template);
        template.setDeletedDate();
        mealTemplateRepository.save(template);
    }

    @Transactional
    public Long addRecord(Long templateId, Long servingId, Float amount) {
        var template = getById(templateId);
        ensureCanWrite(template);

        foodServingApi.getById(servingId);

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        var record = new MealTemplateFoodRecordEntity(templateId, servingId, amount);
        return mealTemplateFoodRecordRepository.save(record).getId();
    }

    @Transactional
    public void updateRecord(Long templateRecordId, Float amount) {
        var record = mealTemplateFoodRecordRepository.findById(templateRecordId)
                .orElseThrow(() -> new EntityNotFoundException("Template record not found"));

        var template = getById(record.getMealTemplateId());
        ensureCanWrite(template);

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        record.setAmount(amount);
        mealTemplateFoodRecordRepository.save(record);
    }

    @Transactional
    public void deleteRecord(Long templateRecordId) {
        var record = mealTemplateFoodRecordRepository.findById(templateRecordId)
                .orElseThrow(() -> new EntityNotFoundException("Template record not found"));

        var template = getById(record.getMealTemplateId());
        ensureCanWrite(template);

        mealTemplateFoodRecordRepository.delete(record);
    }

    private void ensureCanWrite(MealTemplateEntity template) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(template.getDiaryProfileId());
        mealPolicy.ensureIsOwner(currentUser, ownerId);
    }

    public MealTemplateEntity getById(Long id) {
        return mealTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found"));
    }
}
