package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record GoalRequest(
        @DecimalMin(value = "20.0") @DecimalMax(value = "300.0") Float plannedWeight,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate plannedEndDate,
        @Valid List<GoalNutrientRequest> nutrientGoals
) {
}
