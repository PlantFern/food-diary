package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.common.services.NutrientService;
import com.github.plantfern.foodDiary.food.api.EntityStatus;
import com.github.plantfern.foodDiary.food.domain.entities.ProductNutrientEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.ProductNutrientRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class ProductNutrientService {


    private final ProductService productService;
    private final NutrientService nutrientService;
    private final FoodPolicy foodPolicy;
    private final CurrentUser currentUser;
    private final ProductNutrientRepository productNutrientRepository;

    public ProductNutrientService(ProductService productService, NutrientService nutrientService, FoodPolicy foodPolicy, CurrentUser currentUser, ProductNutrientRepository productNutrientRepository) {
        this.productService = productService;
        this.nutrientService = nutrientService;
        this.foodPolicy = foodPolicy;
        this.currentUser = currentUser;
        this.productNutrientRepository = productNutrientRepository;
    }

    public Long addToProduct(Long productId, Long nutrientId, Float amount){

        if(amount < 0)
            throw new IllegalArgumentException("Amount cannot be less than 0");

        var product = productService.getById(productId);

        var nutrient = nutrientService.getById(nutrientId);

        if(product.getEntityStatus().getCode().equals(EntityStatus.ACTIVE.name()))
            foodPolicy.ensureModeration(currentUser);
        else
            foodPolicy.ensureIsOwnerOrModeration(currentUser, product.getCreatedById());

        return productNutrientRepository.save(
                new ProductNutrientEntity(
                        amount,
                        product.getId(),
                        nutrient.getId()
                )
        ).getId();
    }

    public Long updateProductNutrient(Long productNutrientId, Float amount){

        if(amount < 0)
            throw new IllegalArgumentException("Amount cannot be less than 0");

        var productNutrient = productNutrientRepository
                .findById(productNutrientId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Product nutrient with such id not found")
                );

        if(productNutrient.getProduct().getEntityStatus().getCode().equals(EntityStatus.ACTIVE.name()))
            foodPolicy.ensureModeration(currentUser);
        else
            foodPolicy.ensureIsOwnerOrModeration(currentUser, productNutrient.getProduct().getCreatedById());

        productNutrient.setAmount(amount);

        return productNutrientRepository.save(productNutrient).getId();
    }

    public void deleteFromProduct(Long productNutrientId) {
        var productNutrient = productNutrientRepository
                .findById(productNutrientId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Product nutrient with such id not found")
                );

        if(productNutrient.getProduct().getEntityStatus().getCode().equals(EntityStatus.ACTIVE.name()))
            foodPolicy.ensureModeration(currentUser);
        else
            foodPolicy.ensureIsOwnerOrModeration(currentUser, productNutrient.getProduct().getCreatedById());

        productNutrientRepository.delete(productNutrient);
    }
}
