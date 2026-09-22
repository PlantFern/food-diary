package com.github.plantfern.foodDiary.meals.domain.services;

import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.food.api.apis.ProductApi;
import com.github.plantfern.foodDiary.meals.domain.MealPolicy;
import com.github.plantfern.foodDiary.meals.domain.entities.MealEntity;
import com.github.plantfern.foodDiary.meals.domain.entities.MealFoodRecordEntity;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealFoodRecordRepository;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealRepository;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealTypeRepository;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Service
@AllArgsConstructor
public class MealFoodRecordService {

    private final MealFoodRecordRepository mealFoodRecordRepository;
    private final MealRepository mealRepository;
    private final FoodServingApi foodServingApi;
    private final DiaryProfileApi diaryProfileApi;
    private final MealPolicy mealPolicy;
    private final CurrentUser currentUser;
    private final MealTypeRepository mealTypeRepository;
    private final ProductApi productApi;
    // ProductService / FoodApi — для quick nutrient product

    @Transactional
    public Long add(
            Long diaryProfileId,
            Long mealId,
            Long mealTypeId,
            LocalDate date,
            Long servingId,
            Float amount,
            LocalTime eatenAt
    ) {

        if(amount == null || amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than 0");

        var ownerId = diaryProfileApi.getOwnerUserIdInternal(
                diaryProfileId
        );
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        foodServingApi.getById(servingId);

        MealEntity meal;
        if (mealId != null) {

            meal = mealRepository.findById(mealId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Meal not found")
                    );

            if(!meal.getDiaryProfileId().equals(diaryProfileId))
                throw new AccessDeniedException("Meal belongs to another profile");
        }
        else {

            if(mealTypeId == null || date == null)
                throw new IllegalArgumentException("mealTypeId and date required when mealId is null");

            mealTypeRepository
                    .findById(mealTypeId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Meal type not found")
                    );

            meal = mealRepository
                    .findAllByDiaryProfileIdAndMealTypeIdAndDate(
                            diaryProfileId,
                            mealTypeId,
                            date
                    )
                    .orElseGet(
                            () -> mealRepository.save(
                                    new MealEntity(diaryProfileId, mealTypeId, null, date)
                            )
                    );
        }

        return mealFoodRecordRepository
                .save(
                        new MealFoodRecordEntity(
                                servingId,
                                meal.getId(),
                                amount,
                                eatenAt
                        )
                ).getId();
    }

    @Transactional
    public Long addWithQuickProduct(
            Long diaryProfileId,
            Long mealId,
            Long mealTypeId,
            LocalDate date,
            LocalTime eatenAt,
            Float amount,
            String description,
            Float gramWeight,
            Map<Long, Float> nutrients
    ) {

        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        Long servingId = productApi.createNutrientRecordingProduct(
                description,
                gramWeight,
                nutrients
        );

        return add(
                diaryProfileId,
                mealId,
                mealTypeId,
                date,
                servingId,
                amount,
                eatenAt
        );
    }

    @Transactional
    public void update(Long recordId, Float amount, LocalTime eatenAt) {
        var record = mealFoodRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Food record not found"));

        var meal = mealRepository.findById(record.getMealId())
                .orElseThrow(() -> new EntityNotFoundException("Meal not found"));

        var ownerId = diaryProfileApi.getOwnerUserIdInternal(meal.getDiaryProfileId());
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        if (amount != null) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0");
            record.setAmount(amount);
        }
        if (eatenAt != null) {
            record.setEatenAt(eatenAt);
        }

        mealFoodRecordRepository.save(record);
    }

    @Transactional
    public void delete(Long recordId) {
        var record = mealFoodRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Food record not found"));

        var meal = mealRepository.findById(record.getMealId())
                .orElseThrow(() -> new EntityNotFoundException("Meal not found"));

        var ownerId = diaryProfileApi.getOwnerUserIdInternal(meal.getDiaryProfileId());
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        mealFoodRecordRepository.delete(record);
    }
}
