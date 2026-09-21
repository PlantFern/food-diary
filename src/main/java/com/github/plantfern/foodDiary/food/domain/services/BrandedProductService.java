package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.food.api.dto.BrandedProductDto;
import com.github.plantfern.foodDiary.food.domain.entities.BrandedProductEntity;
import com.github.plantfern.foodDiary.food.domain.mappers.BrandedProductMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.BrandedProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BrandedProductService {

    private final ProductService productService;
    private final FoodServingService foodServingService;
    private final BrandedProductMapper brandedProductMapper;
    private final BrandedProductRepository brandedProductRepository;

    public BrandedProductService(ProductService productService, FoodServingService foodServingService, BrandedProductMapper brandedProductMapper, BrandedProductRepository brandedProductRepository) {
        this.productService = productService;
        this.foodServingService = foodServingService;
        this.brandedProductMapper = brandedProductMapper;
        this.brandedProductRepository = brandedProductRepository;
    }

    public BrandedProductDto create(
            Long productId,
            String barcode,
            Long baseServingId
    ) {

        if(barcode.isEmpty())
            throw new IllegalArgumentException("Barcode cannot be empty");

        if(!productService.existsById(productId))
            throw new EntityNotFoundException("Product not found");

        if(!foodServingService.existsById(baseServingId))
            throw new EntityNotFoundException("Product serving not found");

        return brandedProductMapper.toDto(
                brandedProductRepository.save(
                        new BrandedProductEntity(
                                productId,
                                barcode,
                                baseServingId
                        )
                )
        );
    }
}
