package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import java.time.LocalDate;

public record GoalDto(
        Long id,
        Long diaryProfileId,
        Long plannedWeight,
        LocalDate startDate,
        LocalDate actualEndDate,
        LocalDate plannedEndDate,
        Long createdById
) {
}
