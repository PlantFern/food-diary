package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GoalNutrientRequest(
        @NotNull @Positive Long nutrientId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) Float amount
) {
}
