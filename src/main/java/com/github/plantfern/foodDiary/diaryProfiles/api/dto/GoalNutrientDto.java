package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalEntity;

public record GoalNutrientDto(
        Long goalId,
        Long nutrientId,
        Float amount
) {
}
