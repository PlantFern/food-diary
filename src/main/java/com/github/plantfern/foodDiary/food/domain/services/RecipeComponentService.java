package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.food.api.EntityStatus;
import com.github.plantfern.foodDiary.food.domain.entities.RecipeComponentEntity;
import com.github.plantfern.foodDiary.food.domain.entities.RecipeEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.FoodServingRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.RecipeComponentRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RecipeComponentService {

    private final RecipeService recipeService;
    private final FoodServingRepository foodServingRepository;
    private final RecipeComponentRepository recipeComponentRepository;
    private final FoodPolicy foodPolicy;
    private final CurrentUser currentUser;

    public RecipeComponentService(
            RecipeService recipeService,
            FoodServingRepository foodServingRepository,
            RecipeComponentRepository recipeComponentRepository,
            FoodPolicy foodPolicy,
            CurrentUser currentUser
    ) {
        this.recipeService = recipeService;
        this.foodServingRepository = foodServingRepository;
        this.recipeComponentRepository = recipeComponentRepository;
        this.foodPolicy = foodPolicy;
        this.currentUser = currentUser;
    }

    public Long addToRecipe(Long recipeId, Long productServingId, Float amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        var recipe = recipeService.getById(recipeId);

        foodServingRepository
                .findById(productServingId)
                .orElseThrow(() -> new EntityNotFoundException("Food serving with such id not found"));

        ensureCanEditRecipe(recipe);

        return recipeComponentRepository.save(
                new RecipeComponentEntity(recipe.getId(), productServingId, amount)
        ).getId();
    }

    public Long updateComponent(Long recipeComponentId, Float amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        var component = recipeComponentRepository
                .findById(recipeComponentId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe component with such id not found"));

        ensureCanEditRecipe(component.getRecipe());

        component.setAmount(amount);
        return recipeComponentRepository.save(component).getId();
    }

    public void deleteFromRecipe(Long recipeComponentId) {
        var component = recipeComponentRepository
                .findById(recipeComponentId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe component with such id not found"));

        ensureCanEditRecipe(component.getRecipe());

        recipeComponentRepository.delete(component);
    }

    private void ensureCanEditRecipe(RecipeEntity recipe) {
        if (recipe.getEntityStatus().getCode().equals(EntityStatus.ACTIVE.name())) {
            foodPolicy.ensureModeration(currentUser);
        } else {
            foodPolicy.ensureIsOwnerOrModeration(currentUser, recipe.getCreatedById());
        }
    }
}
