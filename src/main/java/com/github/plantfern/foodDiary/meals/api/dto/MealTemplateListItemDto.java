package com.github.plantfern.foodDiary.meals.api.dto;

import java.time.LocalTime;

public record MealTemplateListItemDto(
        Long id,
        String name,
        LocalTime scheduledTime,
        Long frequency,
        Integer itemCount,
        Float totalKcal
) {
}
