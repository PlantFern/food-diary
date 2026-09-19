package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

public record GoalNutrientRequest(
        Long nutrientId,
        Float amount
) {
}
