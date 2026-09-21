package com.github.plantfern.foodDiary.food.web.requests;

public record RecipeComponentRequest(
        Long productServingId,
        Float amount
) {
}
