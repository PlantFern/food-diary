package com.github.plantfern.foodDiary.food.api.dto;

public record RecipeListItemDto(
        Long recipeId,
        String recipeCode,
        String recipeName,
        String photoPath,
        Float totalWeightGrams,
        Float kcalPer100g,
        Boolean isFavorite
) {
}
