package com.github.plantfern.foodDiary.meals.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
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

import java.time.LocalTime;


@Service
@AllArgsConstructor
public class MealTemplateService {

    private final MealTemplateRepository mealTemplateRepository;
    private final MealTemplateFoodRecordRepository mealTemplateFoodRecordRepository;
    private final FoodServingApi foodServingApi;
    private final DiaryProfileApi diaryProfileApi;
    private final MealPolicy mealPolicy;
    private final CurrentUser currentUser;

    @Transactional
    public Long create(
            Long diaryProfileId,
            String name,
            LocalTime scheduledTime,
            Long frequency,
            Long startedAt
    ) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        return mealTemplateRepository.save(
                new MealTemplateEntity(diaryProfileId, name, scheduledTime, frequency, startedAt)
        ).getId();
    }

    @Transactional
    public void update(
            Long templateId,
            String name,
            LocalTime scheduledTime,
            Long frequency,
            Long startedAt
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
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        var template = getById(templateId);
        ensureCanWrite(template);

        foodServingApi.getById(servingId);

        return mealTemplateFoodRecordRepository.save(
                new MealTemplateFoodRecordEntity(templateId, servingId, amount)
        ).getId();
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

    MealTemplateEntity getById(Long id) {
        return mealTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found"));
    }
}
