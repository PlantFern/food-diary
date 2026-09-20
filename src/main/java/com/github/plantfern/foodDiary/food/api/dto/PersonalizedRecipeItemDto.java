package com.github.plantfern.foodDiary.food.api.dto;

import java.util.List;

public record PersonalizedRecipeItemDto(
        Long recipeId,
        String code,
        String name,
        String description,
        String recipe,
        String photoPath,
        Float totalWeightGrams,
        boolean isPublic,
        boolean isFavorite,
        List<RecipeComponentItemDto> recipeComponents
) {
}
