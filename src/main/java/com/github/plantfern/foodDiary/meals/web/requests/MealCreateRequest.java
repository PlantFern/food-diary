package com.github.plantfern.foodDiary.meals.web.requests;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

public record MealCreateRequest(
        Long mealTypeId,
        @Nullable Long templateId,
        @Nullable String photoPath,
        @Nullable LocalDateTime eatenAt
) {
}
