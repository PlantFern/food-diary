package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.common.storage.FileService;
import com.github.plantfern.foodDiary.common.storage.PhotoPathResolver;
import com.github.plantfern.foodDiary.common.storage.StorageFolder;
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
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@Service
public class RecipeService {

    private final CurrentUser currentUser;
    private final RecipeRepository recipeRepository;
    private final EntityStatusRepository entityStatusRepository;
    private final FoodPolicy foodPolicy;
    private final RecipeComponentService recipeComponentService;
    private final PhotoPathResolver photoPathResolver;
    private final FileService fileService;


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

    @Transactional
    public String setPhoto(Long recipeId, MultipartFile file) {
        var recipe = getById(recipeId);
        boolean hasPhoto = recipe.getPhotoPath() != null && !recipe.getPhotoPath().isBlank();

        if (hasPhoto) {
            foodPolicy.ensureModeration(currentUser);
        } else {
            foodPolicy.ensureIsOwner(currentUser, recipe.getCreatedById());
        }

        var uploaded = fileService.upload(file, StorageFolder.FOOD_RECIPES);
        String path = "/api/files/" + uploaded.getId();
        recipe.setPhotoPath(path);
        recipeRepository.save(recipe);
        return path;
    }

    @Transactional
    public void countTotalWeight(Long recipeId) {

        var recipe = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        recipe.setTotalWeightGrams(recipeComponentService.countWeightByRecipeId(recipeId));
        recipeRepository.save(recipe);
    }

    @Transactional(readOnly = true)
    RecipeEntity getById(Long id) {
        return recipeRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe with such id not found"));
    }
}
