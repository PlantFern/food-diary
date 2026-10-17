package com.github.plantfern.foodDiary.meals.domain.services;

import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryCommentApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.meals.domain.MealPolicy;
import com.github.plantfern.foodDiary.meals.domain.entities.MealEntity;
import com.github.plantfern.foodDiary.meals.domain.entities.MealFoodRecordEntity;
import com.github.plantfern.foodDiary.meals.domain.entities.MealTemplateEntity;
import com.github.plantfern.foodDiary.meals.domain.entities.MealTemplateFoodRecordEntity;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealFoodRecordRepository;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealRepository;
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

@Service
@AllArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealFoodRecordRepository mealFoodRecordRepository;
    private final MealTemplateRepository mealTemplateRepository;
    private final MealTemplateFoodRecordRepository mealTemplateFoodRecordRepository;
    private final DiaryProfileApi diaryProfileApi;
    private final MealPolicy mealPolicy;
    private final CurrentUser currentUser;
    private final DiaryCommentApi diaryCommentApi;

    @Transactional
    public Long create(Long diaryProfileId, Long mealTypeId, LocalDate mealDate) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        var meal = new MealEntity(diaryProfileId, mealTypeId, null, mealDate);
        return mealRepository.save(meal).getId();
    }

    @Transactional
    public Long applyTemplate(
            Long diaryProfileId,
            Long mealTypeId,
            Long templateId,
            LocalDate mealDate,
            LocalTime eatenAt
    ) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        MealTemplateEntity template = mealTemplateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found"));

        if (!template.getDiaryProfileId().equals(diaryProfileId)) {
            throw new EntityNotFoundException("Template belongs to another profile");
        }

        LocalTime time = eatenAt != null
                ? eatenAt
                : (template.getScheduledTime() != null ? template.getScheduledTime() : LocalTime.of(12, 0));

        var meal = mealRepository
                .findAllByDiaryProfileIdAndMealTypeIdAndDate(diaryProfileId, mealTypeId, mealDate)
                .orElseGet(() -> mealRepository.save(new MealEntity(diaryProfileId, mealTypeId, null, mealDate)));

        List<MealTemplateFoodRecordEntity> records =
                mealTemplateFoodRecordRepository.findAllByMealTemplateId(templateId);

        for (var tr : records) {
            mealFoodRecordRepository.save(
                    new MealFoodRecordEntity(
                            tr.getServingId(),
                            meal.getId(),
                            tr.getAmount(),
                            time
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

        if (mealTypeId != null) meal.setMealTypeId(mealTypeId);
        if (photoPath != null) meal.setPhotoPath(photoPath);
        mealRepository.save(meal);
    }

    @Transactional
    public void updatePhoto(Long mealId, String photoPath) {
        var meal = getById(mealId);
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(meal.getDiaryProfileId());
        mealPolicy.ensureIsOwner(currentUser, ownerId);
        meal.setPhotoPath(photoPath);
        mealRepository.save(meal);
    }

    @Transactional
    public void softDelete(Long mealId) {
        var meal = getById(mealId);
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(meal.getDiaryProfileId());
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        diaryCommentApi.deleteByCommentableInternal(CommentableType.MEAL, mealId);
        meal.setDeletedDate();
        mealRepository.save(meal);
    }

    public MealEntity getById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found"));
    }
}
