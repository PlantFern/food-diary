package com.github.plantfern.foodDiary.meals.web.requests;

import java.time.LocalDate;


public record MealFoodRecordCreateRequest(
        Long servingId,
        Float amount,
        LocalDate date
) {
}
