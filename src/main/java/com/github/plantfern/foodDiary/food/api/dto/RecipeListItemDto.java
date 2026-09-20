package com.github.plantfern.foodDiary.food.api.dto;

public record RecipeListItemDto(
        Long recipeId,
        String name,
        String photoPath
) {
}
