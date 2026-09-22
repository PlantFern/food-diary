package com.github.plantfern.foodDiary.meals.api.dto;

public record MealFoodRecordItemDto(
        Long recordId,
        Long servingId,
        String productDescription,
        Float amount,
        Float servingAmount,
        Float gramWeight,
        String servingUnitCode,
        Float kcal
) {
}
