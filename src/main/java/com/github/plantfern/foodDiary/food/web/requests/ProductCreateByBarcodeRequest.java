package com.github.plantfern.foodDiary.food.web.requests;

import java.util.List;

public record ProductCreateByBarcodeRequest(
        String description,
        Long categoryId,
        String photoPath,
        String barcode,
        Float gramWeight,
        List<NutrientRequest> nutrients,
        String frontPhotoPath,
        String productCompositionPhotoPath,
        String productNutritionPhotoPath,
        String barcodePhotoPath
) {
}
