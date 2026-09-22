package com.github.plantfern.foodDiary.meals.domain.services;

import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.meals.domain.MealPolicy;
import com.github.plantfern.foodDiary.meals.domain.entities.MealFoodRecordEntity;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealFoodRecordRepository;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealRepository;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@AllArgsConstructor
public class MealFoodRecordService {

    private final MealFoodRecordRepository mealFoodRecordRepository;
    private final MealRepository mealRepository;
    private final FoodServingApi foodServingApi;
    private final DiaryProfileApi diaryProfileApi;
    private final MealPolicy mealPolicy;
    private final CurrentUser currentUser;
    // ProductService / FoodApi — для quick nutrient product

    @Transactional
    public Long add(Long mealId, Long servingId, Float amount, LocalTime eatenAt) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        var meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found"));

        var ownerId = diaryProfileApi.getOwnerUserIdInternal(meal.getDiaryProfileId());
        mealPolicy.ensureIsOwner(currentUser, ownerId);

        foodServingApi.getById(servingId);

        return mealFoodRecordRepository.save(
                new MealFoodRecordEntity(servingId, mealId, amount, eatenAt)
        ).getId();
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
