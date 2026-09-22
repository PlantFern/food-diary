package com.github.plantfern.foodDiary.meals.web.requests;

import java.util.Map;
import java.util.Set;

public record QuickProductCreateRequest(
        String name,
        String description,
        Float weight,
        Map<Long, Float> nutrients
) {
}
