package com.github.plantfern.foodDiary.meals.api.dto;

public record DayNutrientStatDto(
        Long nutrientId,
        String nutrientCode,
        Float targetAmount,
        Float factAmount,
        Float remainingAmount,
        Float percentOfTarget
) {
}
