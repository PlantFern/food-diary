package com.github.plantfern.foodDiary.meals.api.dto;

import java.time.LocalTime;

/**
 * API representation of a single food record.
 * Contains scalar values only so JSON serialization does not traverse lazy JPA relations.
 */
public record MealFoodRecordDto(
        Long id,
        Long mealId,
        Long servingId,
        Float amount,
        LocalTime eatenAt
) {
}
