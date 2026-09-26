package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import com.github.plantfern.foodDiary.diaryProfiles.api.ActivityLevel;
import com.github.plantfern.foodDiary.diaryProfiles.api.GoalType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExtendedProfileDataRequest(
        @NotNull Float height,
        @NotNull LocalDate birthDate,
        @NotNull Long genderId,
        @NotNull Float weight,
        @NotNull Float plannedWeight,
        @NotNull ActivityLevel activityLevel,
        @NotNull GoalType goalType
) {
}
