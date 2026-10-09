package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import com.github.plantfern.foodDiary.diaryProfiles.api.ActivityLevel;
import com.github.plantfern.foodDiary.diaryProfiles.api.GoalType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExtendedProfileDataRequest(
        @Min(50) @NotNull Float height,
        @NotNull LocalDate birthDate,
        @NotNull Long genderId,
        @Min(30) @Max(400) @NotNull Float weight,
        @Min(30) @Max(400) @NotNull Float plannedWeight,
        @NotNull ActivityLevel activityLevel,
        @NotNull GoalType goalType
) {
}
