package com.github.plantfern.foodDiary.diaryProfiles.domain.dto;

public record ProfileFeatureSettingsDto(
        Long id,
        Long diaryProfileId,          // только id, не весь DiaryProfileEntity
        Boolean showSleep,
        Boolean showSleepLogs,
        Boolean showWeight,
        Boolean showWeightLogs,
        Boolean showAllergensWarning,
        Long createdBy
) {
}
