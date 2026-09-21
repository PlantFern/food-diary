package com.github.plantfern.foodDiary.food.api.dto;

import java.util.List;

public record RecipeDetailDto(
        Long recipeId,
        String recipeName,
        String recipeDescription,
        String recipe,
        String photoPath,
        Float totalWeightGrams,
        String entityStatusCode,
        Boolean isPublic,
        List<RecipeComponentDto> components
) {
}
