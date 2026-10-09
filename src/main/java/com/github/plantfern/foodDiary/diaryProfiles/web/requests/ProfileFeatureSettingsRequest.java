package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public record ProfileFeatureSettingsRequest(
        @NotNull Boolean showSleep,
        @NotNull Boolean showSleepLogs,
        @NotNull Boolean showWeight,
        @NotNull Boolean showWeightLogs,
        @NotNull Boolean showAllergensWarning,
        Set<Long> hiddenNutrients
) { }
