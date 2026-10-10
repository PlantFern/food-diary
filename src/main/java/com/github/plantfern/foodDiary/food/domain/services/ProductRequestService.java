package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.common.storage.PhotoPathResolver;
import com.github.plantfern.foodDiary.food.api.EntityStatus;
import com.github.plantfern.foodDiary.food.domain.entities.ProductRequestEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.ProductRequestRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class ProductRequestService {

    private final ProductService productService;
    private final ProductRequestRepository productRequestRepository;
    private final FoodPolicy foodPolicy;
    private final CurrentUser currentUser;
    private final PhotoPathResolver photoPathResolver;

    public ProductRequestService(
            ProductService productService,
            ProductRequestRepository productRequestRepository,
            FoodPolicy foodPolicy,
            CurrentUser currentUser,
            PhotoPathResolver photoPathResolver) {
        this.productService = productService;
        this.productRequestRepository = productRequestRepository;
        this.foodPolicy = foodPolicy;
        this.currentUser = currentUser;
        this.photoPathResolver = photoPathResolver;
    }

    public Long create(
            Long productId,
            Long frontPhotoPathId,
            Long productCompositionPhotoPathId,
            Long productNutritionPhotoPathId,
            Long barcodePhotoPathId
    ) {

        String frontPhotoPath = photoPathResolver.fromFileId(frontPhotoPathId);
        String productCompositionPhotoPath = photoPathResolver.fromFileId(productCompositionPhotoPathId);
        String productNutritionPhotoPath = photoPathResolver.fromFileId(productNutritionPhotoPathId);
        String barcodePhotoPath = photoPathResolver.fromFileId(barcodePhotoPathId);

        var product = productService.getById(productId);

        foodPolicy.ensureIsOwnerOrModeration(currentUser, product.getCreatedById());

        if(productRequestRepository.existsByProductId(product.getId()))
            throw new IllegalArgumentException("Product request with such product id already exists");

        return productRequestRepository.save(
                new ProductRequestEntity(
                        productId,
                        frontPhotoPath,
                        productCompositionPhotoPath,
                        productNutritionPhotoPath,
                        barcodePhotoPath,
                        currentUser.requireId()
                )
        ).getId();
    }

    public Long update(
            Long productRequestId,
            Long frontPhotoPathId,
            Long productCompositionPhotoPathId,
            Long productNutritionPhotoPathId,
            Long barcodePhotoPathId
    ) {

        var productRequest = getById(productRequestId);

        if(productRequest.getProduct().getEntityStatus().getCode().equals(EntityStatus.ACTIVE.name()))
            foodPolicy.ensureModeration(currentUser);
        else
            foodPolicy.ensureIsOwnerOrModeration(currentUser, productRequest.getProduct().getCreatedById());

        String frontPhotoPath = photoPathResolver.fromFileId(frontPhotoPathId);
        String productCompositionPhotoPath = photoPathResolver.fromFileId(productCompositionPhotoPathId);
        String productNutritionPhotoPath = photoPathResolver.fromFileId(productNutritionPhotoPathId);
        String barcodePhotoPath = photoPathResolver.fromFileId(barcodePhotoPathId);

        productRequest.setFrontPhotoPath(frontPhotoPath);
        productRequest.setProductCompositionPhotoPath(productCompositionPhotoPath);
        productRequest.setProductNutritionPhotoPath(productNutritionPhotoPath);
        productRequest.setBarcodePhotoPath(barcodePhotoPath);

        return  productRequestRepository
                .save(productRequest)
                .getId();
    }

    ProductRequestEntity getById(Long id) {

        return productRequestRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Product request with such id not found")
                );
    }
}
