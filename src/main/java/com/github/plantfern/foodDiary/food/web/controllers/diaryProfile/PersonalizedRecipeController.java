package com.github.plantfern.foodDiary.food.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeDetailDto;
import com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeListItemDto;
import com.github.plantfern.foodDiary.food.domain.services.RecipeQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/food/recipes")
public class PersonalizedRecipeController {

    private final RecipeQueryService recipeQueryService;

    public PersonalizedRecipeController(RecipeQueryService recipeQueryService) {
        this.recipeQueryService = recipeQueryService;
    }

    @GetMapping("/diary-profile/{diaryProfileId}")
    public List<PersonalizedRecipeListItemDto> getAllPersonalized(
            @PathVariable Long diaryProfileId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean onlyFavorites,
            @RequestParam(required = false) Boolean onlyMy
    ) {
        return recipeQueryService.getAllPersonalized(diaryProfileId, query, onlyFavorites, onlyMy);
    }

    @GetMapping("/diary-profile/{diaryProfileId}/recipes/{recipeId}")
    public PersonalizedRecipeDetailDto getDetail(
            @PathVariable Long diaryProfileId,
            @PathVariable Long recipeId
    ) {
        return recipeQueryService.getPersonalizedDetail(diaryProfileId, recipeId);
    }
}
