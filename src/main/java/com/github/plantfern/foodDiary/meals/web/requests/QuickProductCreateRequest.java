package com.github.plantfern.foodDiary.meals.web.requests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

public record QuickProductCreateRequest(
        Long mealId,
        Long mealTypeId,
        LocalDate date,
        LocalTime eatenAt,
        Float amount,
        String description,
        Float gramWeight,
        Map<Long, Float> nutrients
) {
}
