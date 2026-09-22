package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.common.services.NutrientService;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.ProfileFeatureSettingsApi;
import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.api.dto.*;
import com.github.plantfern.foodDiary.food.domain.mappers.FoodServingMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.FavoriteFoodRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.VProductBasicRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.VProductNutrientRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.food.web.requests.ProductSearchRequest;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    public ProductQueryService(
            ProfileFeatureSettingsApi profileFeatureSettingsApi,
            NutrientService nutrientService,
            VProductBasicRepository vProductBasicRepository,
            VProductNutrientRepository vProductNutrientRepository,
            CurrentUser currentUser,
            FoodPolicy foodPolicy, DiaryProfileApi diaryProfileApi, FoodServingService foodServingService, FavoriteFoodRepository favoriteFoodRepository, FoodServingMapper foodServingMapper) {
        this.profileFeatureSettingsApi = profileFeatureSettingsApi;
        this.nutrientService = nutrientService;
        this.vProductBasicRepository = vProductBasicRepository;
        this.vProductNutrientRepository = vProductNutrientRepository;
        this.currentUser = currentUser;
        this.foodPolicy = foodPolicy;
        this.diaryProfileApi = diaryProfileApi;
        this.foodServingService = foodServingService;
        this.favoriteFoodRepository = favoriteFoodRepository;
        this.foodServingMapper = foodServingMapper;
    }

    @Transactional(readOnly = true)
    public Page<PersonalizedProductListItemDto> getAllPersonalized(
            Long diaryProfileId,
            @Nullable PersonalizedProductSearchRequest request,
            Pageable pageable
    ){

        var diaryProfile = diaryProfileApi.getByIdInternal(diaryProfileId);
        foodPolicy.ensureIsOwner(currentUser, diaryProfile.userId());

        var settings = profileFeatureSettingsApi.getActiveByDiaryProfileInternal(diaryProfileId);
        var hiddenNutrientIds = settings.hiddenNutrientIds();
        var accepted = nutrientService.getFirstByIdNotIn(hiddenNutrientIds);
        var checkedAccepted = hiddenNutrientIds != null
                ? accepted
                : 1L;

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
                checkedAccepted,
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
    public Page<ProductListItemDto> getAll(
            @Nullable ProductSearchRequest request,
            Pageable pageable
    ){

        foodPolicy.ensureModeration(currentUser);

        var req = request != null
                ? request
                : new ProductSearchRequest(null, null);

        return vProductBasicRepository.findList(
                (req.query() == null
                        || req.query().isBlank())
                        ? null
                        : req.query().trim(),
                req.categoryId(),
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

        var hiddenNutrientIds = profileFeatureSettingsApi
                .getActiveByDiaryProfileInternal(diaryProfileId)
                .hiddenNutrientIds();

        var productNutrients = vProductNutrientRepository.findPersonalizedByProductId(
                diaryProfile.id(),
                hiddenNutrientIds
        );

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
                .orElseThrow();

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
                vProductBasic.isFavorite(),
                foundProductServings,
                productNutrients
        );
    }

    @Transactional(readOnly = true)
    public ProductDetailDto getDetailAboutProduct(
            Long productId
    ) {

        foodPolicy.ensureModeration(currentUser);

        var productNutrients = vProductNutrientRepository
                .findByProductId(productId);

        var foundProductServings = foodServingService
                .getByProductId(productId)
                .stream().map(foodServingMapper::toDto)
                .toList();

        var vProductBasic = vProductBasicRepository
                .findByProductId(
                        productId
                )
                .orElseThrow(
                        () -> new EntityNotFoundException("Product with such product id not found")
                );

        return new ProductDetailDto(
                vProductBasic.getProductId(),
                vProductBasic.getProductCode(),
                vProductBasic.getProductDescription(),
                vProductBasic.getPhotoPath(),
                vProductBasic.getCategoryCode(),
                vProductBasic.getEntityStatusCode(),
                vProductBasic.getDataSourceCode(),
                vProductBasic.getIsPublic(),
                foundProductServings,
                productNutrients
        );
    }
}
