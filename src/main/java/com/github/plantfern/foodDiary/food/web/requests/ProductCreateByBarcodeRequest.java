package com.github.plantfern.foodDiary.food.web.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


import java.util.List;


public record ProductCreateByBarcodeRequest(
        @NotBlank String description,
        Long categoryId,
        Long photoPathId,
        @NotBlank String barcode,
        @NotNull @DecimalMin(value = "0", inclusive = false) Float gramWeight,
        @NotEmpty @Valid List<NutrientRequest> nutrients,
        Long frontPhotoPathId,
        Long productCompositionPhotoPathId,
        Long productNutritionPhotoPathId,
        Long barcodePhotoPathId
) {
}
