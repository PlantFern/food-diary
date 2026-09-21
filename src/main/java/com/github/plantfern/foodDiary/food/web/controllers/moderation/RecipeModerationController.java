package com.github.plantfern.foodDiary.food.web.controllers.moderation;


import com.github.plantfern.foodDiary.food.api.dto.RecipeDetailDto;
import com.github.plantfern.foodDiary.food.api.dto.RecipeListItemDto;
import com.github.plantfern.foodDiary.food.domain.services.RecipeQueryService;
import com.github.plantfern.foodDiary.food.domain.services.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food/moderation/recipes")
public class RecipeModerationController {

    private final RecipeQueryService recipeQueryService;
    private final RecipeService recipeService;

    public RecipeModerationController(RecipeQueryService recipeQueryService, RecipeService recipeService) {
        this.recipeQueryService = recipeQueryService;
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeListItemDto>> getAll(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(recipeQueryService.getAll(query));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDetailDto> getDetail(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeQueryService.getDetail(recipeId));
    }

    @PatchMapping("/{recipeId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Long recipeId) {
        recipeService.activate(recipeId);
    }

    @PatchMapping("/{recipeId}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long recipeId) {
        recipeService.archive(recipeId);
    }
}

