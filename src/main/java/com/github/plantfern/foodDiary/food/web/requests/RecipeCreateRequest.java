package com.github.plantfern.foodDiary.food.web.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecipeCreateRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String recipe,
        @NotBlank String photoPath,
        @NotNull @DecimalMin(value = "0", inclusive = false) Float totalWeightGrams,
        @NotEmpty List<RecipeComponentRequest> components
) {
}
