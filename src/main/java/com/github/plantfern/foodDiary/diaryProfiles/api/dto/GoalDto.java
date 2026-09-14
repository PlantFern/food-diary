package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;

import java.time.LocalDate;

public record GoalDto(
        Long diaryProfileId,
        Long plannedWeight,
        LocalDate startDate,
        LocalDate actualEndDate,
        LocalDate plannedEndDate,
        Long createdById
) {
}
