package com.github.plantfern.foodDiary.meals.web.requests;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;


public record MealTemplateApplyRequest(
        @NotNull Long mealTypeId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mealDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime eatenAt
) {
}
