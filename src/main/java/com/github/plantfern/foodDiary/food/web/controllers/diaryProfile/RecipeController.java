package com.github.plantfern.foodDiary.food.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.food.domain.services.RecipeComponentService;
import com.github.plantfern.foodDiary.food.domain.services.RecipeService;
import com.github.plantfern.foodDiary.food.web.requests.RecipeComponentRequest;
import com.github.plantfern.foodDiary.food.web.requests.RecipeCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/food/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeComponentService recipeComponentService;

    public RecipeController(RecipeService recipeService, RecipeComponentService recipeComponentService) {
        this.recipeService = recipeService;
        this.recipeComponentService = recipeComponentService;
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody RecipeCreateRequest request) {
        Long recipeId = recipeService.create(
                request.name(),
                request.description(),
                request.recipe(),
                request.photoPath()
        );

        if (request.components() != null) {
            for (var c : request.components()) {
                recipeComponentService.addToRecipe(recipeId, c.productServingId(), c.amount());
            }
        }

        recipeService.countTotalWeight(recipeId);

        return ResponseEntity.ok(recipeId);
    }

    @PutMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long recipeId, @RequestBody RecipeCreateRequest request) {
        recipeService.update(
                recipeId,
                request.name(),
                request.description(),
                request.recipe(),
                request.photoPath()
        );
    }

    @DeleteMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long recipeId) {
        recipeService.softDelete(recipeId);
    }

    @PostMapping("/{recipeId}/components")
    public ResponseEntity<Long> addComponent(
            @PathVariable Long recipeId,
            @RequestBody RecipeComponentRequest request
    ) {
        return ResponseEntity.ok(
                recipeComponentService.addToRecipe(recipeId, request.productServingId(), request.amount())
        );
    }

    @DeleteMapping("/components/{recipeComponentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComponent(@PathVariable Long recipeComponentId) {
        recipeComponentService.deleteFromRecipe(recipeComponentId);
    }
}
