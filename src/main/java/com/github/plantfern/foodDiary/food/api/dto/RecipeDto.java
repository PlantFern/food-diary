package com.github.plantfern.foodDiary.food.api.dto;

public record RecipeDto(
        Long id,
        String code,
        String name,
        String description,
        String recipe,
        String photoPath,
        Float totalWeightGrams,
        boolean isPublic,
        Long createdById
) {
}