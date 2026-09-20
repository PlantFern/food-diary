package com.github.plantfern.foodDiary.food.api.dto;

public record ProductListItemDto(
        Long productId,
        String productCode,
        String productDescription,
        String photoPath,
        String categoryCode,
        String dataSourceCode
) { }
