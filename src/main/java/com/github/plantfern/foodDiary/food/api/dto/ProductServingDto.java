package com.github.plantfern.foodDiary.food.api.dto;

public record ProductServingDto (
        Long servingId,
        Long productId,
        String productDescription,
        Long servingAmount,
        Float gramWeight,
        String servingUnitCode,
        String nutrientPer100g
) {
}
