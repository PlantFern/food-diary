package com.github.plantfern.foodDiary.meals.api.dto;

public record MealTemplateFoodRecordItemDto(
        Long id,
        Long servingId,
        String productDescription,
        Float amount,
        Float servingAmount,
        Float gramWeight,
        String servingUnitCode
) {
}
