package com.github.plantfern.foodDiary.meals.web.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;

public record MealTemplateCreateRequest(
        @NotBlank String name,
        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime scheduledTime,
        @NotNull @Positive Long frequency,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedAt
) {
}
