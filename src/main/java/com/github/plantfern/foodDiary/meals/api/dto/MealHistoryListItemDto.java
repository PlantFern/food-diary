package com.github.plantfern.foodDiary.meals.api.dto;

import java.time.LocalDate;
import java.util.List;

public record MealHistoryListItemDto (
        Long mealId,
        LocalDate date,
        String mealTypeCode,
        String title,             // "Sat, Sept 19 Breakfast"
        Float totalKcal,
        Integer itemCount,
        List<String> productNamesPreview
){
}
