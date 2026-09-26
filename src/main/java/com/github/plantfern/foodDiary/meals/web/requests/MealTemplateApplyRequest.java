package com.github.plantfern.foodDiary.meals.web.requests;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;

public record MealTemplateApplyRequest(
        Long mealTypeId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate mealDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime eatenAt
) {
}
