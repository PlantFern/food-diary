package com.github.plantfern.foodDiary.food.web.controllers.diaryProfile;


import org.springframework.validation.annotation.Validated;

import com.github.plantfern.foodDiary.food.api.DataSource;
import com.github.plantfern.foodDiary.food.domain.services.*;
import com.github.plantfern.foodDiary.food.web.requests.ProductCreateByBarcodeRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/api/food/products")
public class ProductController {

    private static final long SERVING_UNIT_GRAMS = 1L;

    private final ProductService productService;
    private final BrandedProductService brandedProductService;
    private final FoodServingService foodServingService;
    private final ProductNutrientService productNutrientService;
    private final ProductRequestService productRequestService;


    @PostMapping("/barcode")
    public ResponseEntity<Long> createByBarcode(
            @Valid @RequestBody ProductCreateByBarcodeRequest request
    ) {
        Long productId = productService.create(
                request.description(),
                request.categoryId(),
                request.photoPathId(),
                DataSource.BARCODE,
                request.barcode()
        );

        // Default servings: 1 g and 100 g (base = 100 g or user gramWeight)
        foodServingService.createForProduct(
                productId,
                1F,
                1F,
                SERVING_UNIT_GRAMS,
                "1 г"
        );

        float baseGrams = request.gramWeight() != null ? request.gramWeight() : 100F;
        var baseServing = foodServingService.createForProduct(
                productId,
                1F,
                baseGrams,
                SERVING_UNIT_GRAMS,
                baseGrams == 100F ? "100 г" : baseGrams + " г"
        );

        if (baseGrams != 100F) {
            foodServingService.createForProduct(
                    productId,
                    1F,
                    100F,
                    SERVING_UNIT_GRAMS,
                    "100 г"
            );
        }

        brandedProductService.create(productId, request.barcode(), baseServing.id());

        for (var n : request.nutrients()) {
            productNutrientService.addToProduct(productId, n.nutrientId(), n.amount());
        }

        boolean anyRequestPhoto =
                request.frontPhotoPathId() != null
                        || request.productCompositionPhotoPathId() != null
                        || request.productNutritionPhotoPathId() != null
                        || request.barcodePhotoPathId() != null;

        if (anyRequestPhoto) {
            productRequestService.create(
                    productId,
                    request.frontPhotoPathId(),
                    request.productCompositionPhotoPathId(),
                    request.productNutritionPhotoPathId(),
                    request.barcodePhotoPathId()
            );
        }

        return ResponseEntity.ok(productId);
    }

    @PatchMapping(value = "/{productId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> setPhoto(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(productService.setPhoto(productId, file));
    }
}
