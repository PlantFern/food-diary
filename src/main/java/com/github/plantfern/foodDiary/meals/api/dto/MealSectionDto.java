package com.github.plantfern.foodDiary.meals.api.dto;

import java.util.List;

public record MealSectionDto(
        Long mealId,
        Long mealTypeId,
        String mealTypeCode,
        Long generatedFromTemplateId,
        List<MealFoodRecordItemDto> records,
        Float totalPrimaryNutrient
) {
}
