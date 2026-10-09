package com.github.plantfern.foodDiary.food.web.requests;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


public record RecipeComponentRequest(
        @NotNull Long productServingId,
        @NotNull @DecimalMin(value = "0", inclusive = false) Float amount
) {
}
