package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeDetailDto;
import com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeListItemDto;
import com.github.plantfern.foodDiary.food.api.dto.RecipeDetailDto;
import com.github.plantfern.foodDiary.food.api.dto.RecipeListItemDto;
import com.github.plantfern.foodDiary.food.domain.mappers.RecipeComponentMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.FavoriteFoodRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.RecipeComponentRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.VRecipeBasicRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecipeQueryService {

    private final VRecipeBasicRepository vRecipeBasicRepository;
    private final RecipeComponentRepository recipeComponentRepository;
    private final FavoriteFoodRepository favoriteFoodRepository;
    private final DiaryProfileApi diaryProfileApi;
    private final FoodPolicy foodPolicy;
    private final CurrentUser currentUser;
    private final RecipeComponentMapper recipeComponentMapper;

    public RecipeQueryService(
            VRecipeBasicRepository vRecipeBasicRepository,
            RecipeComponentRepository recipeComponentRepository,
            FavoriteFoodRepository favoriteFoodRepository,
            DiaryProfileApi diaryProfileApi,
            FoodPolicy foodPolicy,
            CurrentUser currentUser,
            RecipeComponentMapper recipeComponentMapper) {
        this.vRecipeBasicRepository = vRecipeBasicRepository;
        this.recipeComponentRepository = recipeComponentRepository;
        this.favoriteFoodRepository = favoriteFoodRepository;
        this.diaryProfileApi = diaryProfileApi;
        this.foodPolicy = foodPolicy;
        this.currentUser = currentUser;
        this.recipeComponentMapper = recipeComponentMapper;
    }

    public List<PersonalizedRecipeListItemDto> getAllPersonalized(
            Long diaryProfileId,
            String query,
            Boolean onlyFavorites,
            Boolean onlyMy
    ) {
        var diaryProfile = diaryProfileApi.getByIdInternal(diaryProfileId);
        foodPolicy.ensureIsOwner(currentUser, diaryProfile.userId());

        return vRecipeBasicRepository.findPersonalizedList(
                diaryProfileId,
                currentUser.requireId(),
                (query == null || query.isBlank()) ? null : query.trim(),
                Boolean.TRUE.equals(onlyFavorites),
                Boolean.TRUE.equals(onlyMy)
        );
    }

    public List<RecipeListItemDto> getAll(String query) {
        foodPolicy.ensureModeration(currentUser);
        return vRecipeBasicRepository.findList(
                (query == null || query.isBlank()) ? null : query.trim()
        );
    }

    public PersonalizedRecipeDetailDto getPersonalizedDetail(Long diaryProfileId, Long recipeId) {
        var diaryProfile = diaryProfileApi.getByIdInternal(diaryProfileId);
        foodPolicy.ensureIsOwner(currentUser, diaryProfile.userId());

        var basic = vRecipeBasicRepository.findByRecipeId(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        boolean isFavorite = favoriteFoodRepository.existsByDiaryProfileIdAndItemTypeAndItemId(
                diaryProfileId, ItemType.RECIPE, recipeId);

        var components = recipeComponentRepository
                .findAllByRecipeId(recipeId)
                .stream()
                .map(recipeComponentMapper::toDto)
                .toList();

        return new PersonalizedRecipeDetailDto(
                basic.getRecipeId(),
                basic.getRecipeName(),
                basic.getRecipeDescription(),
                basic.getRecipe(),
                basic.getPhotoPath(),
                basic.getTotalWeightGrams(),
                basic.getEntityStatusCode(),
                basic.getIsPublic(),
                isFavorite,
                components
        );
    }

    public RecipeDetailDto getDetail(Long recipeId) {
        foodPolicy.ensureModeration(currentUser);

        var basic = vRecipeBasicRepository.findByRecipeId(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        var components = recipeComponentRepository
                .findAllByRecipeId(recipeId)
                .stream()
                .map(recipeComponentMapper::toDto)
                .toList();

        return new RecipeDetailDto(
                basic.getRecipeId(),
                basic.getRecipeName(),
                basic.getRecipeDescription(),
                basic.getRecipe(),
                basic.getPhotoPath(),
                basic.getTotalWeightGrams(),
                basic.getEntityStatusCode(),
                basic.getIsPublic(),
                components
        );
    }
}