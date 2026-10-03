package com.github.plantfern.foodDiary.meals.api.dto;

import java.time.LocalDate;
import java.util.List;

public record RecentDayFoodGroupDto(
        LocalDate date,
        List<RecentFoodItemDto> items
) {
}
