package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Map;

public record ProfileDataWithGoalRequest(
        @Min(50) Float height,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
        Long genderId,
        @Min(30) Float weight,
        @Min(30) Float plannedWeight,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate plannedEndDate,
        Map<Long, Float> goalNutrientMap
) {
}
