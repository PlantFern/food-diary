package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.food.api.dto.FoodServingDto;
import com.github.plantfern.foodDiary.food.domain.entities.FoodServingEntity;
import com.github.plantfern.foodDiary.food.domain.mappers.FoodServingMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.FoodServingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FoodServingService implements FoodServingApi {

    private final FoodServingRepository foodServingRepository;
    private final ProductService productService;
    private final ServingUnitService servingUnitService;
    private final FoodServingMapper foodServingMapper;

    public FoodServingService(
            FoodServingRepository foodServingRepository,
            ProductService productService, ServingUnitService servingUnitService, FoodServingMapper foodServingMapper) {
        this.foodServingRepository = foodServingRepository;
        this.productService = productService;
        this.servingUnitService = servingUnitService;
        this.foodServingMapper = foodServingMapper;
    }

    public FoodServingDto createForProduct(
            Long productId,
            Long amount,
            Float gramWeight,
            Long servingUnitId,
            String description
    ) {

        if(amount <= 0)
            throw new IllegalArgumentException("Amount must be greater then 0");

        if(gramWeight <= 0)
            throw new IllegalArgumentException("Grem weight must be greater then 0");

        if(!servingUnitService.existsById(servingUnitId))
            throw new EntityNotFoundException("Serving unit not found");

        if(!productService.existsById(productId))
            throw new EntityNotFoundException("Product not found");

        return foodServingMapper.toDto(foodServingRepository.save(
                new FoodServingEntity(
                        productId,
                        ItemType.PRODUCT,
                        amount,
                        gramWeight,
                        servingUnitId,
                        description
                )
        ));
    }

    public List<FoodServingEntity> getByProductId(Long productId) {

        return foodServingRepository.findAllByItemIdAndItemType(productId, ItemType.PRODUCT);
    }

    public boolean existsById(Long foodServingId) {
        return foodServingRepository.existsById(foodServingId);
    }


    // region Override

    @Override
    public FoodServingDto getById(Long foodServingId) {
        return foodServingRepository
                .findById(foodServingId)
                .map(foodServingMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Serving with such id not found")
                );
    }
    //endregion
}
