package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeDetailDto;
import com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeListItemDto;
import com.github.plantfern.foodDiary.food.domain.mappers.RecipeComponentMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.FavoriteFoodRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.RecipeComponentRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.VRecipeBasicRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@AllArgsConstructor
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


    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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
}
