package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import java.time.LocalDate;
import java.util.Map;

public record ProfileDataWithGoalRequest(
        Float height,
        LocalDate birthDate,
        Long genderId,
        Float weight,
        Float plannedWeight,
        LocalDate plannedEndDate,
        Map<Long, Float> goalNutrientMap
) {
}
