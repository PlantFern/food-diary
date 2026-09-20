package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.domain.entities.FoodServingEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.FoodServingRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FoodServingService {

    private final FoodServingRepository foodServingRepository;

    public FoodServingService(
            FoodServingRepository foodServingRepository
    ) {
        this.foodServingRepository = foodServingRepository;
    }

    public List<FoodServingEntity> getByProductId(Long productId) {

        return foodServingRepository.findAllByItemIdAndItemType(productId, ItemType.PRODUCT);
    }
}
