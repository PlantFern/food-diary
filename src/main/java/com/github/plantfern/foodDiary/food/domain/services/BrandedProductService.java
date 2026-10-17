package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.food.api.dto.BrandedProductDto;
import com.github.plantfern.foodDiary.food.domain.entities.BrandedProductEntity;
import com.github.plantfern.foodDiary.food.domain.mappers.BrandedProductMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.BrandedProductRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BrandedProductService {

    private final FoodServingService foodServingService;
    private final BrandedProductMapper brandedProductMapper;
    private final BrandedProductRepository brandedProductRepository;
    private final ProductRepository productRepository;


    @Transactional
    public BrandedProductDto create(
            Long productId,
            String barcode,
            Long baseServingId
    ) {

        if (barcode == null || barcode.isBlank())
            throw new IllegalArgumentException("Barcode cannot be empty");

        String normalized = barcode.trim();

        if (brandedProductRepository.existsByBarcodeAndDeletedAtIsNull(normalized))
            throw new IllegalArgumentException("Product with this barcode already exists");

        if (!productRepository.existsById(productId))
            throw new EntityNotFoundException("Product not found");

        if (!foodServingService.existsById(baseServingId))
            throw new EntityNotFoundException("Product serving not found");

        return brandedProductMapper.toDto(
                brandedProductRepository.save(
                        new BrandedProductEntity(
                                productId,
                                normalized,
                                baseServingId
                        )
                )
        );
    }
}
