package com.github.plantfern.foodDiary.food.api.dto;

public record RecipeComponentDto(
        Long componentId,
        Long productServingId,
        Float amount,
        String productDescription,
        String servingUnitCode
) {
}
