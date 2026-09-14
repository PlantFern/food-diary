package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import java.time.LocalDateTime;

public record ProfileFeatureSettingsDto(
        Long id,
        Long diaryProfileId,
        Boolean showSleep,
        Boolean showSleepLogs,
        Boolean showWeight,
        Boolean showWeightLogs,
        Boolean showAllergensWarning,
        Long createdById,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
}
