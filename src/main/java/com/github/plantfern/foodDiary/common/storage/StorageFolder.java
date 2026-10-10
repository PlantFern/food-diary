package com.github.plantfern.foodDiary.common.storage;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum StorageFolder {
    FOOD_PRODUCTS("food/products"),
    FOOD_RECIPES("food/recipes"),
    FOOD_PRODUCT_REQUEST("food/product-requests"),
    MEALS_MEALS("meals/meals");

    private final String path;
}
