package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import java.time.LocalDate;
import java.util.Set;

public record GoalDto(
        Long id,
        Long diaryProfileId,
        Long plannedWeight,
        LocalDate startDate,
        LocalDate actualEndDate,
        LocalDate plannedEndDate,
        Long createdById,
        Set<GoalNutrientDto> nutrientSet
) {
}
