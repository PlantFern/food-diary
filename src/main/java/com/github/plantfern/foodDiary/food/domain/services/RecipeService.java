package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.common.storage.PhotoPathResolver;
import com.github.plantfern.foodDiary.food.api.EntityStatus;
import com.github.plantfern.foodDiary.food.domain.entities.RecipeEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.EntityStatusRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.RecipeRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@AllArgsConstructor
@Service
public class RecipeService {

    private final CurrentUser currentUser;
    private final RecipeRepository recipeRepository;
    private final EntityStatusRepository entityStatusRepository;
    private final FoodPolicy foodPolicy;
    private final RecipeComponentService recipeComponentService;
    private final PhotoPathResolver photoPathResolver;


    @Transactional
    public Long create(
            String name,
            String description,
            String recipeText,
            Long photoPathId
    ) {
        var entityStatus = entityStatusRepository
                .findByCode(EntityStatus.DRAFT.name())
                .orElseThrow(() -> new EntityNotFoundException("Entity status with such code not found"));

        String photoPath = photoPathResolver.fromFileId(photoPathId);

        var savedRecipe = recipeRepository.save(
                new RecipeEntity(
                        name,
                        description,
                        recipeText,
                        photoPath,
                        false,
                        entityStatus.getId(),
                        currentUser.requireId()
                )
        );

        savedRecipe.assignCode();
        return recipeRepository.save(savedRecipe).getId();
    }

    @Transactional
    public void update(
            Long recipeId,
            String name,
            String description,
            String recipeText,
            Long photoPathId
    ) {

        var foundRecipe = getById(recipeId);

        if (foundRecipe.getEntityStatus().getCode().equals(EntityStatus.ACTIVE.name())) {
            foodPolicy.ensureModeration(currentUser);
        } else {
            foodPolicy.ensureIsOwnerOrModeration(currentUser, foundRecipe.getCreatedById());
        }

        String photoPath = photoPathResolver.fromFileId(photoPathId);

        foundRecipe.setName(name);
        foundRecipe.setDescription(description);
        foundRecipe.setRecipe(recipeText);
        foundRecipe.setPhotoPath(photoPath);

        recipeRepository.save(foundRecipe);
    }

    @Transactional
    public void activate(Long recipeId) {
        foodPolicy.ensureModeration(currentUser);

        var foundRecipe = getById(recipeId);

        var entityStatus = entityStatusRepository
                .findByCode(EntityStatus.ACTIVE.name())
                .orElseThrow(() -> new EntityNotFoundException("Entity status with such code not found"));

        foundRecipe.setEntityStatusId(entityStatus.getId());
        foundRecipe.setPublic(true);

        recipeRepository.save(foundRecipe);
    }

    @Transactional
    public void archive(Long recipeId) {
        foodPolicy.ensureModeration(currentUser);

        var foundRecipe = getById(recipeId);

        var entityStatus = entityStatusRepository
                .findByCode(EntityStatus.ARCHIVED.name())
                .orElseThrow(() -> new EntityNotFoundException("Entity status with such code not found"));

        foundRecipe.setEntityStatusId(entityStatus.getId());

        recipeRepository.save(foundRecipe);
    }

    @Transactional
    public void softDelete(Long recipeId) {
        var foundRecipe = getById(recipeId);

        if (foundRecipe.getEntityStatus().getCode().equals(EntityStatus.ACTIVE.name())) {
            foodPolicy.ensureModeration(currentUser);
        } else {
            foodPolicy.ensureIsOwnerOrModeration(currentUser, foundRecipe.getCreatedById());
        }

        foundRecipe.softDelete();
        recipeRepository.save(foundRecipe);
    }

    @Transactional(readOnly = true)
    public void countTotalWeight(Long recipeId) {

        var recipe = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        recipe.setTotalWeightGrams(recipeComponentService.countWeightByRecipeId(recipeId));
    }

    @Transactional(readOnly = true)
    RecipeEntity getById(Long id) {
        return recipeRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe with such id not found"));
    }
}