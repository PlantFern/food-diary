package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import java.util.List;
import java.util.Set;

public record ProfileFeatureSettingsRequest(
        Boolean showSleep,
        Boolean showSleepLogs,
        Boolean showWeight,
        Boolean showWeightLogs,
        Boolean showAllergensWarning,
        Set<Long> hiddenNutrients
) { }
