package com.github.plantfern.foodDiary.food.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.domain.services.FavoriteFoodService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/api/food/diary-profile/{diaryProfileId}/favorites")
public class FavoriteFoodController {

    private final FavoriteFoodService favoriteFoodService;


    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProduct(
            @PathVariable Long diaryProfileId,
            @PathVariable Long productId
    ) {
        favoriteFoodService.add(diaryProfileId, ItemType.PRODUCT, productId);
    }

    @DeleteMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(
            @PathVariable Long diaryProfileId,
            @PathVariable Long productId
    ) {
        favoriteFoodService.remove(diaryProfileId, ItemType.PRODUCT, productId);
    }

    @GetMapping("/products/{productId}")
    public boolean isProductFavorite(
            @PathVariable Long diaryProfileId,
            @PathVariable Long productId
    ) {
        return favoriteFoodService.isFavorite(diaryProfileId, ItemType.PRODUCT, productId);
    }

    @GetMapping("/products")
    public List<Long> listFavoriteProducts(@PathVariable Long diaryProfileId) {
        return favoriteFoodService.listItemIds(diaryProfileId, ItemType.PRODUCT);
    }

    @PostMapping("/recipes/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRecipe(
            @PathVariable Long diaryProfileId,
            @PathVariable Long recipeId
    ) {
        favoriteFoodService.add(diaryProfileId, ItemType.RECIPE, recipeId);
    }

    @DeleteMapping("/recipes/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRecipe(
            @PathVariable Long diaryProfileId,
            @PathVariable Long recipeId
    ) {
        favoriteFoodService.remove(diaryProfileId, ItemType.RECIPE, recipeId);
    }

    @GetMapping("/recipes/{recipeId}")
    public boolean isRecipeFavorite(
            @PathVariable Long diaryProfileId,
            @PathVariable Long recipeId
    ) {
        return favoriteFoodService.isFavorite(diaryProfileId, ItemType.RECIPE, recipeId);
    }

    @GetMapping("/recipes")
    public List<Long> listFavoriteRecipes(@PathVariable Long diaryProfileId) {
        return favoriteFoodService.listItemIds(diaryProfileId, ItemType.RECIPE);
    }
}
