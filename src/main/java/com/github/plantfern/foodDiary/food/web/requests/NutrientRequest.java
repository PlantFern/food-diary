package com.github.plantfern.foodDiary.food.web.requests;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


public record NutrientRequest(
        @NotNull Long nutrientId,
        @NotNull @DecimalMin(value = "0", inclusive = false) Float amount
) {
}
