package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.common.domain.services.NutrientService;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.ProfileFeatureSettingsApi;
import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.api.dto.*;
import com.github.plantfern.foodDiary.food.domain.mappers.FoodServingMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.FavoriteFoodRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.VProductBasicRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.VProductNutrientRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@AllArgsConstructor
@Transactional
@Service
public class ProductQueryService {

    private final ProfileFeatureSettingsApi profileFeatureSettingsApi;
    private final NutrientService nutrientService;
    private final VProductBasicRepository vProductBasicRepository;
    private final VProductNutrientRepository vProductNutrientRepository;
    private final CurrentUser currentUser;
    private final FoodPolicy foodPolicy;
    private final DiaryProfileApi diaryProfileApi;
    private final FoodServingService foodServingService;
    private final FavoriteFoodRepository favoriteFoodRepository;
    private final FoodServingMapper foodServingMapper;


    @Transactional(readOnly = true)
    public Page<PersonalizedProductListItemDto> getAllPersonalized(
            Long diaryProfileId,
            @Nullable PersonalizedProductSearchRequest request,
            Pageable pageable
    ){

        var diaryProfile = diaryProfileApi.getByIdInternal(diaryProfileId);
        foodPolicy.ensureIsOwner(currentUser, diaryProfile.userId());

        var settings = profileFeatureSettingsApi.getActiveByDiaryProfileInternal(diaryProfileId);
        Set<Long> hiddenNutrientIds = settings.hiddenNutrientIds() != null
                ? settings.hiddenNutrientIds()
                : Set.of();

        Long mainNutrientId = nutrientService.getFirstByIdNotIn(hiddenNutrientIds);
        if (mainNutrientId == null) {
            mainNutrientId = 1L;
        }

        var req = request != null
                ? request
                : new PersonalizedProductSearchRequest(
                        null,
                        null,
                        null,
                        null
                );

        return vProductBasicRepository.findPersonalizedList(
                diaryProfileId,
                mainNutrientId,
                currentUser.requireId(),
                (req.query() == null
                        || req.query().isBlank())
                        ? null
                        : req.query().trim(),
                req.categoryId(),
                Boolean.TRUE.equals(req.onlyFavorites()),
                Boolean.TRUE.equals(req.onlyMy()),
                pageable
        );
    }

    @Transactional(readOnly = true)
    public PersonalizedProductDetailDto getPersonalizedDetailAboutProduct(
            Long diaryProfileId,
            Long productId
    ) {

        var diaryProfile = diaryProfileApi.getByIdInternal(diaryProfileId);
        foodPolicy.ensureIsOwner(currentUser, diaryProfile.userId());

        var settings = profileFeatureSettingsApi.getActiveByDiaryProfileInternal(diaryProfileId);
        Set<Long> hiddenNutrientIds = settings.hiddenNutrientIds() != null
                ? settings.hiddenNutrientIds()
                : Set.of();

        // All values for the product (including ones that may later be masked as null)
        Map<Long, ProductNutrientItemDto> byNutrientId = vProductNutrientRepository
                .findByProductId(productId)
                .stream()
                .collect(Collectors.toMap(
                        ProductNutrientItemDto::nutrientId,
                        Function.identity(),
                        (a, b) -> a
                ));

        // Full catalog: missing or hidden → amount null (UI shows dash)
        var productNutrients = nutrientService.findAllOrdered().stream()
                .map(n -> {
                    String unitCode = n.getNutrientUnit() != null ? n.getNutrientUnit().getCode() : null;
                    if (hiddenNutrientIds.contains(n.getId())) {
                        return new ProductNutrientItemDto(n.getId(), n.getCode(), null, unitCode);
                    }
                    var found = byNutrientId.get(n.getId());
                    if (found != null) {
                        return found;
                    }
                    return new ProductNutrientItemDto(n.getId(), n.getCode(), null, unitCode);
                })
                .toList();

        var foundProductServings = foodServingService
                .getByProductId(productId)
                .stream().map(foodServingMapper::toDto)
                .toList();

        var vProductBasic = vProductBasicRepository
                .findByProductIdPersonalized(
                        diaryProfileId,
                        productId,
                        currentUser.requireId()
                )
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        boolean isFavorite = favoriteFoodRepository.existsByDiaryProfileIdAndItemTypeAndItemId(
                diaryProfileId,
                ItemType.PRODUCT,
                productId);

        return new PersonalizedProductDetailDto(
                vProductBasic.productId(),
                vProductBasic.productCode(),
                vProductBasic.productDescription(),
                vProductBasic.photoPath(),
                vProductBasic.categoryCode(),
                vProductBasic.entityStatusCode(),
                vProductBasic.dataSourceCode(),
                vProductBasic.isPublic(),
                isFavorite,
                foundProductServings,
                productNutrients
        );
    }

}
