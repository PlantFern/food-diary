package com.github.plantfern.foodDiary.meals.api.dto;

import java.time.LocalDate;
import java.util.List;

public record DayMealsDto(
        LocalDate date,
        List<DayNutrientStatDto> targets,
        List<MealSectionDto> sections,
        List<MealTemplateListItemDto> pendingTemplates
) {
}
