package com.github.plantfern.foodDiary.food.api.dto;

public record FoodServingDto(
        Long id,
        String itemType,
        Long itemId,
        Float amount,
        Float gramWeight,
        ServingUnitDto servingUnit,
        String description
) { }
