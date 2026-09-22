package com.github.plantfern.foodDiary.food.api.apis;

import com.github.plantfern.foodDiary.food.api.dto.FoodServingDto;
import com.github.plantfern.foodDiary.food.api.dto.ProductServingDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public interface FoodServingApi {

    FoodServingDto getById(Long foodServingId);

    Map<Long, ProductServingDto> getInfoByServingIds(Collection<Long> servingIds, Long acceptedNutrientId);
}
