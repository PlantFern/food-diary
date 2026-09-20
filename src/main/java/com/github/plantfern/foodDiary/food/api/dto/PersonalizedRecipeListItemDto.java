package com.github.plantfern.foodDiary.food.api.dto;

public record PersonalizedRecipeListItemDto(
        Long recipeId,
        String code,
        String name,
        String description,
        String recipe,
        String photoPath,
        Float totalWeightGrams,
        boolean isPublic,
        Long createdById,
        Boolean isFavorite
) {
}
