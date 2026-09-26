package com.github.plantfern.foodDiary.meals.web.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record MealCreateRequest(
        @NotNull Long mealTypeId,
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        @Nullable Long templateId,
        @Nullable String photoPath,
        @Nullable
        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
        @JsonFormat(pattern = "HH:mm") LocalTime eatenAt
        ) {
}
