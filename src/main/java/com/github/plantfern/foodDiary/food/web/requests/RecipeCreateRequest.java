package com.github.plantfern.foodDiary.food.web.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RecipeCreateRequest(
        @NotBlank String name,
        String description,
        String recipe,
        Long photoPathId,
        Float totalWeightGrams,
        @Valid List<RecipeComponentRequest> components
) {
}
