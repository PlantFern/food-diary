package com.github.plantfern.foodDiary.meals.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MealTemplateDto(
        Long id,
        Long diaryProfileId,
        String name,
        LocalTime scheduledTime,
        Long frequency,
        LocalDate startedAt,
        List<MealTemplateFoodRecordItemDto> records
) {
}
