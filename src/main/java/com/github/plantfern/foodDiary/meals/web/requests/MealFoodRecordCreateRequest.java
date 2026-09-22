package com.github.plantfern.foodDiary.meals.web.requests;

import java.time.LocalDate;
import java.time.LocalTime;


public record MealFoodRecordCreateRequest(
        Long mealId,
        Long mealTypeId,
        Long servingId,
        Float amount,
        LocalDate date,
        LocalTime eatenAt
) {
}
