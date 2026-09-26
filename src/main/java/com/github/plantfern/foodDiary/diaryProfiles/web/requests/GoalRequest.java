package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import java.time.LocalDate;
import java.util.List;

public record GoalRequest(
        Float plannedWeight,
        LocalDate startDate,
        LocalDate plannedEndDate,
        List<GoalNutrientRequest> nutrientGoals
) {
}
