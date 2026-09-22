package com.github.plantfern.foodDiary.meals.domain.services;

import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.meals.domain.MealPolicy;
import com.github.plantfern.foodDiary.meals.domain.entities.MealEntity;
import com.github.plantfern.foodDiary.meals.domain.entities.MealFoodRecordEntity;
import com.github.plantfern.foodDiary.meals.domain.repositories.*;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalTime;

@Service
@AllArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealTypeRepository mealTypeRepository;
    private final MealTemplateRepository mealTemplateRepository;
    private final MealTemplateFoodRecordRepository mealTemplateFoodRecordRepository;
    private final MealFoodRecordRepository mealFoodRecordRepository;
    private final DiaryProfileApi diaryProfileApi;
    private final MealPolicy mealPolicy; // или Food/Meal policy
    private final CurrentUser currentUser;

    @Transactional
    public Long create(Long diaryProfileId, Long mealTypeId, LocalDate mealDate) {

        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        mealTypeRepository.findById(mealTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Meal type not found"));

        return mealRepository.save(
                new MealEntity(
                        diaryProfileId,
                        mealTypeId,
                        null,
                        mealDate)
        ).getId();
    }

    @Transactional
    public Long applyTemplate(
            Long diaryProfileId,
            Long mealTypeId,
            Long templateId,
            LocalDate mealDate,
            LocalTime eatenAt) {

        var ownerId = diaryProfileApi
                .getOwnerUserIdInternal(diaryProfileId);

        mealPolicy.ensureIsOwner(currentUser, ownerId);

        var template = mealTemplateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found"));

        if (!template.getDiaryProfileId().equals(diaryProfileId)) {
            throw new AccessDeniedException("Template belongs to another profile");
        }

        var meal = mealRepository.save(
                new MealEntity(
                        diaryProfileId,
                        mealTypeId,
                        template.getId(),
                        mealDate
                )
        );

        var templateRecords = mealTemplateFoodRecordRepository.findAllByMealTemplateId(templateId);
        for (var tr : templateRecords) {
            mealFoodRecordRepository.save(
                    new MealFoodRecordEntity(
                            tr.getServingId(),
                            meal.getId(),
                            tr.getAmount(),
                            eatenAt
                    )
            );
        }

        return meal.getId();
    }

    @Transactional
    public void update(Long mealId, Long mealTypeId, String photoPath) {
        var meal = getById(mealId);
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(meal.getDiaryProfileId());
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        if (mealTypeId != null) {
            mealTypeRepository.findById(mealTypeId)
                    .orElseThrow(() -> new EntityNotFoundException("Meal type not found"));
            meal.setMealTypeId(mealTypeId);
        }
        meal.setPhotoPath(photoPath);
        mealRepository.save(meal);
    }

    @Transactional
    public void softDelete(Long mealId) {
        var meal = getById(mealId);
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(meal.getDiaryProfileId());
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        meal.setDeletedDate();
        mealRepository.save(meal);
    }

    MealEntity getById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found"));
    }
}
