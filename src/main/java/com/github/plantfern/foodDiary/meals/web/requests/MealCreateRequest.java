package com.github.plantfern.foodDiary.meals.web.requests;

import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MealCreateRequest(
        Long mealTypeId,
        LocalDate date,
        @Nullable Long templateId,
        @Nullable String photoPath,
        @Nullable LocalDateTime eatenAt
) {
}
