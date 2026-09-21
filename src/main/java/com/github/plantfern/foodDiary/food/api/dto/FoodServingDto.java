package com.github.plantfern.foodDiary.food.api.dto;

public record FoodServingDto(
        Long id,
        String itemType,
        String itemId,
        String amount,
        String gramWeight,
        ServingUnitDto servingUnit,
        String description
) { }
