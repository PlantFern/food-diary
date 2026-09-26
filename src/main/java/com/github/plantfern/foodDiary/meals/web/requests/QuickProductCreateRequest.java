package com.github.plantfern.foodDiary.meals.web.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

public record QuickProductCreateRequest(
        @NotNull @Positive Long mealId,
        @NotNull @Positive Long mealTypeId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime eatenAt,
        @NotNull @Positive Float amount,
        @NotBlank String description,
        @NotNull @Positive Float gramWeight,
        @NotEmpty Map<Long, Float> nutrients
) {
}
