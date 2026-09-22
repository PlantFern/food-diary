package com.github.plantfern.foodDiary.food.web.requests;

public record ProductSearchRequest(
        String query,
        Long categoryId
) {
}
