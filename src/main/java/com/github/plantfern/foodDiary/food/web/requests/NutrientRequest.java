package com.github.plantfern.foodDiary.food.web.requests;

public record NutrientRequest(
        Long nutrientId,
        Float amount
) {
}
