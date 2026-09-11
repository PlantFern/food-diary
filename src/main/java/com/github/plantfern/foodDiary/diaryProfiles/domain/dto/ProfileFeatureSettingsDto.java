package com.github.plantfern.foodDiary.diaryProfiles.domain.dto;

public record ProfileFeatureSettingsDto(
        Long id,
        Long diaryProfileId,
        Boolean showSleep,
        Boolean showSleepLogs,
        Boolean showWeight,
        Boolean showWeightLogs,
        Boolean showAllergensWarning,
        Long createdById
) {
}
