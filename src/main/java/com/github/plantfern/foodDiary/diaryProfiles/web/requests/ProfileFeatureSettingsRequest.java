package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import java.util.List;

public record ProfileFeatureSettingsRequest(
        Boolean showSleep,
        Boolean showSleepLogs,
        Boolean showWeight,
        Boolean showWeightLogs,
        Boolean showAllergensWarning,
        List<Long> hiddenNutrients
) { }
