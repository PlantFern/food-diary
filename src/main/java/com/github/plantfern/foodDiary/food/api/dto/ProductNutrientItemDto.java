package com.github.plantfern.foodDiary.food.api.dto;

public record ProductNutrientItemDto (
        Long nutrientId,
        String nutrientCode,
        Float amountPer100g,
        String unitCode
) {}
