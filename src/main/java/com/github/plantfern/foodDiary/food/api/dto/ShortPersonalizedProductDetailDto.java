package com.github.plantfern.foodDiary.food.api.dto;

public record ShortPersonalizedProductDetailDto(
        Long productId,
        String productCode,
        String productDescription,
        String photoPath,
        String categoryCode,
        String entityStatusCode,
        String dataSourceCode,
        Boolean isPublic,
        Boolean isFavorite
) { }
