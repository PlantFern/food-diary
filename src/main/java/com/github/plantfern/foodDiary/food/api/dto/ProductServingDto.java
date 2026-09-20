package com.github.plantfern.foodDiary.food.api.dto;

public record ProductServingDto (
        Long servingId,
        Long amount,
        Float gramWeight,
        String servingUnitCode,
        String description
) {
}
