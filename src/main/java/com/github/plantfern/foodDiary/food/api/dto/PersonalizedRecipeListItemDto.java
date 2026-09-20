package com.github.plantfern.foodDiary.food.api.dto;

public record PersonalizedRecipeListItemDto(
        Long recipeId,
        String name,
        String description,
        String photoPath,
        Float totalWeightGrams,
        boolean isPublic,
        Boolean isFavorite
) {
}
