package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GoalNutrientRequest(
        @NotNull Long nutrientId,
        @NotNull @Min(0) Float amount
) {
}
