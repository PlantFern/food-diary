package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalEntity;

public record GoalNutrientDto(
        Long id,
        Long nutrientId,
        Float amount
) {
}
