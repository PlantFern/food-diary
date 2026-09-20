package com.github.plantfern.foodDiary.food.api.dto;

public record PersonalizedProductListItemDto(
        Long productId,
        String productCode,
        String productDescription,
        String photoPath,
        String categoryCode,
        String dataSourceCode,
        Float kcalPer100g,
        Boolean isFavorite
) {
}
