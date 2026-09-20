package com.github.plantfern.foodDiary.food.api.dto;

import java.util.List;

public record PersonalizedProductDetailDto(
        Long productId,
        String productCode,
        String productDescription,
        String photoPath,
        String categoryCode,
        String entityStatusCode,
        String dataSourceCode,
        Boolean isPublic,
        Boolean isFavorite,
        List<FoodServingDto> servings,
        List<ProductNutrientItemDto> nutrients
) {
}
