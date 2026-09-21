package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.api.dto.FoodServingDto;
import com.github.plantfern.foodDiary.food.domain.entities.FoodServingEntity;
import com.github.plantfern.foodDiary.food.domain.mappers.FoodServingMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.FoodServingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FoodServingService {

    private final FoodServingRepository foodServingRepository;
    private final ProductService productService;
    private final ServingUnitService servingUnitService;
    private final FoodServingMapper foodServingMapper;

    public FoodServingService(
            FoodServingRepository foodServingRepository,
            ProductService productService, ServingUnitService servingUnitService, FoodServingMapper foodServingMapper) {
        this.foodServingRepository = foodServingRepository;
    }

    public List<FoodServingEntity> getByProductId(Long productId) {

        return foodServingRepository.findAllByItemIdAndItemType(productId, ItemType.PRODUCT);
    }

    public boolean existsById(Long foodServingId) {
        return foodServingRepository.existsById(foodServingId);
    }
}
