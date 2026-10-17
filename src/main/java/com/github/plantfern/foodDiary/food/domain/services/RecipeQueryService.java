package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeDetailDto;
import com.github.plantfern.foodDiary.food.domain.mappers.FoodServingMapper;
import com.github.plantfern.foodDiary.food.api.dto.FoodServingDto;
import com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeListItemDto;
import com.github.plantfern.foodDiary.food.api.dto.ProductServingDto;
import com.github.plantfern.foodDiary.food.api.dto.RecipeComponentDto;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


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
    private final FoodServingApi foodServingApi;
    private final FoodServingService foodServingService;
    private final FoodServingMapper foodServingMapper;


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

        var basic = vRecipeBasicRepository
                .findByRecipeIdPersonalized(
                        diaryProfileId,
                        recipeId,
                        currentUser.requireId()
                )
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        boolean isFavorite = favoriteFoodRepository.existsByDiaryProfileIdAndItemTypeAndItemId(
                diaryProfileId, ItemType.RECIPE, recipeId);

        var componentEntities = recipeComponentRepository.findAllByRecipeId(recipeId);
        var servingIds = componentEntities.stream()
                .map(c -> c.getProductServingId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, ProductServingDto> servingMap = servingIds.isEmpty()
                ? Map.of()
                : foodServingApi.getInfoByServingIds(servingIds, 1L);

        List<RecipeComponentDto> components = componentEntities.stream()
                .map(c -> {
                    var serving = servingMap.get(c.getProductServingId());
                    Float nutrient = null;
                    if (serving != null
                            && serving.gramWeight() != null
                            && serving.nutrientPer100g() != null
                            && c.getAmount() != null) {
                        float servingAmount = serving.servingAmount() != null
                                ? serving.servingAmount() : 1f;
                        nutrient = (c.getAmount() * serving.gramWeight() * servingAmount / 100f)
                                * serving.nutrientPer100g();
                    }
                    return new RecipeComponentDto(
                            c.getId(),
                            c.getProductServingId(),
                            c.getAmount(),
                            serving != null ? serving.productDescription() : null,
                            serving != null ? serving.servingUnitCode() : null,
                            serving != null ? serving.gramWeight() : null,
                            nutrient
                    );
                })
                .toList();

        var recipeServings = foodServingService
                .getByRecipeId(recipeId)
                .stream()
                .map(foodServingMapper::toDto)
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
                components,
                recipeServings
        );
    }
}
