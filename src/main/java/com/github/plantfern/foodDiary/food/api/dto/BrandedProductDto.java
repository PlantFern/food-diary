package com.github.plantfern.foodDiary.food.api.dto;

public record BrandedProductDto(
        Long id,
        Long productId,
        String barcode,
        Long baseServingId
) {
}
