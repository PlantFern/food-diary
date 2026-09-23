package com.github.plantfern.foodDiary.meals.api.dto;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.SleepLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;

import java.time.LocalDate;
import java.util.List;

public record DayMealsDto(
        LocalDate date,
        Long primaryNutrient,
        String primaryNutrientCode,
        List<DayNutrientStatDto> targets,
        List<MealSectionDto> sections,
        List<MealTemplateListItemDto> pendingTemplates,
        boolean sleepEnabled,
        boolean weightEnabled,
        List<SleepLogDto> sleepForDay,
        WeightLogDto latestWeight
) {
}
