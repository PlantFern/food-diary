package com.github.plantfern.foodDiary.meals.api.dto;

public record RecentFoodItemDto(
        Long id,
        String name,
        String mealTypeCode
) {
}
