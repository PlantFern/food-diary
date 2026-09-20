package com.github.plantfern.foodDiary.food.api.dto;

public record RecipeComponentItemDto(
        Long componentId,
        Long productServingId,
        Float amount,
        String productDescription,
        String servingUnitCode
) {
}
