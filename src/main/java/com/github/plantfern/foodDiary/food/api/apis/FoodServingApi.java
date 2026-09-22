package com.github.plantfern.foodDiary.food.api.apis;

import com.github.plantfern.foodDiary.food.api.dto.FoodServingDto;
import org.springframework.stereotype.Component;

@Component
public interface FoodServingApi {

    FoodServingDto getById(Long foodServingId);
}
