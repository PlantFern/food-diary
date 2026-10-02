package com.github.plantfern.foodDiary.meals.web.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;


public record MealFoodRecordCreateRequest(
        Long mealId,
        @NotNull @Positive Long mealTypeId,
        @NotNull @Positive Long servingId,
        @NotNull @Positive Float amount,
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
        @JsonFormat(pattern = "HH:mm")
        LocalTime eatenAt
) {
}
