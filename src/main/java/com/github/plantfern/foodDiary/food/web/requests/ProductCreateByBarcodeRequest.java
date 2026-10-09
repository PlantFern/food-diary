package com.github.plantfern.foodDiary.food.web.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


import java.util.List;


public record ProductCreateByBarcodeRequest(
        @NotBlank String description,
        @NotNull Long categoryId,
        @NotNull String photoPath,
        @NotBlank String barcode,
        @NotNull @DecimalMin(value = "0", inclusive = false) Float gramWeight,
        @NotEmpty List<NutrientRequest> nutrients,
        @NotNull String frontPhotoPath,
        @NotNull String productCompositionPhotoPath,
        @NotNull String productNutritionPhotoPath,
        @NotNull String barcodePhotoPath
) {
}
