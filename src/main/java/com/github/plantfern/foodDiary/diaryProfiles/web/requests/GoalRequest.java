package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record GoalRequest(
        @NotNull @Min(30) Float plannedWeight,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate plannedEndDate,
        List<GoalNutrientRequest> nutrientGoals
) {
}
