package com.github.plantfern.foodDiary.food.api.dto;

public record ProductSearchRequest(
        String query,
        Long categoryId
) {
}
