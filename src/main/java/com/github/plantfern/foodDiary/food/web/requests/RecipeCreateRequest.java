package com.github.plantfern.foodDiary.food.web.requests;

import java.util.List;

public record RecipeCreateRequest(
        String name,
        String description,
        String recipe,
        String photoPath,
        Float totalWeightGrams,
        List<RecipeComponentRequest> components
) {
}
