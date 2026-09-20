package com.github.plantfern.foodDiary.food.api.dto;

public record PersonalizedProductSearchRequest(
        String query,
        Long categoryId,
        Boolean onlyFavorites,
        Boolean onlyMy
) {
}
