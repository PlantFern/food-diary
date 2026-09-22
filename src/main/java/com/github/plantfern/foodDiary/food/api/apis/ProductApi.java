package com.github.plantfern.foodDiary.food.api.apis;

import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public interface ProductApi {

    Long createNutrientRecordingProduct(
            String description,
            Float gramWeight,
            Map<Long, Float> nutrients
    );
}
