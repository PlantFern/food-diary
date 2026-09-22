package com.github.plantfern.foodDiary.meals.api.dto;

import java.time.LocalDate;
import java.util.List;

public record MealTemplateDto(
        Long id,
        Long diaryProfileId,
        String name,
        LocalDate scheduledTime,
        Long frequency,
        Long startedAt,
        List<MealTemplateFoodRecordItemDto> records
) {
}
