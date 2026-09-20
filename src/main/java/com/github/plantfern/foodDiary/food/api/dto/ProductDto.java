package com.github.plantfern.foodDiary.food.api.dto;

public record ProductDto(
        Long id,
        String code,
        String description,
        Long categoryId,
        String photoPath,
        boolean isPublic,
        Long createdById
) {
}
