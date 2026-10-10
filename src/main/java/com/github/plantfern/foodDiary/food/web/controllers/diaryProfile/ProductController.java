package com.github.plantfern.foodDiary.food.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.common.storage.StorageFolder;
import com.github.plantfern.foodDiary.food.api.DataSource;
import com.github.plantfern.foodDiary.food.domain.services.*;
import com.github.plantfern.foodDiary.food.web.requests.ProductCreateByBarcodeRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/food/products")
public class ProductController {

    private final ProductService productService;
    private final BrandedProductService brandedProductService;
    private final FoodServingService foodServingService;
    private final ProductNutrientService productNutrientService;
    private final ProductRequestService productRequestService;

    public ProductController(
            ProductService productService,
            BrandedProductService brandedProductService,
            FoodServingService foodServingService,
            ProductNutrientService productNutrientService,
            ProductRequestService productRequestService
    ) {
        this.productService = productService;
        this.brandedProductService = brandedProductService;
        this.foodServingService = foodServingService;
        this.productNutrientService = productNutrientService;
        this.productRequestService = productRequestService;
    }

    @PostMapping("/barcode")
    public ResponseEntity<Long> createByBarcode(
            @ModelAttribute ProductCreateByBarcodeRequest request
    ) {
        Long productId = productService.create(
                request.description(),
                request.categoryId(),
                request.photoPathId(),
                DataSource.BARCODE,
                request.barcode()
        );

        var baseServing = foodServingService.createForProduct(
                productId,
                1F,
                request.gramWeight(),
                2L,
                null
        );

        brandedProductService.create(productId, request.barcode(), baseServing.id());

        for (var n : request.nutrients()) {
            productNutrientService.addToProduct(productId, n.nutrientId(), n.amount());
        }

        productRequestService.create(
                productId,
                request.frontPhotoPathId(),
                request.productCompositionPhotoPathId(),
                request.productNutritionPhotoPathId(),
                request.barcodePhotoPathId()
        );

        return ResponseEntity.ok(productId);
    }

    @PatchMapping(value = "/{productId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> setPhoto(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam StorageFolder storageFolder
    ) {
        return ResponseEntity.ok(productService.setPhoto(productId, file, storageFolder));
    }
}
