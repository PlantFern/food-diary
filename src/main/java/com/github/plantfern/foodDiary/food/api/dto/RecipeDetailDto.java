package com.github.plantfern.foodDiary.food.api.dto;

import java.util.List;

public record RecipeDetailDto(
        Long recipeId,
        String recipeCode,
        String recipeName,
        String recipeDescription,
        String recipeText,
        String photoPath,
        Float totalWeightGrams,
        String entityStatusCode,
        Boolean isPublic,
        Boolean isFavorite,
        List<RecipeComponentItemDto> components,
        List<ProductNutrientItemDto> nutrients
) {
}
