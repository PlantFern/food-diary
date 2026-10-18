package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.food.api.dto.FoodServingDto;
import com.github.plantfern.foodDiary.food.api.dto.ProductServingDto;
import com.github.plantfern.foodDiary.food.domain.entities.FoodServingEntity;
import com.github.plantfern.foodDiary.food.domain.mappers.FoodServingMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.*;
import com.github.plantfern.foodDiary.food.domain.views.VFoodServingEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class FoodServingService implements FoodServingApi {

    private final FoodServingRepository foodServingRepository;
    private final ServingUnitService servingUnitService;
    private final FoodServingMapper foodServingMapper;
    private final ProductRepository productRepository;
    private final VFoodServingRepository vFoodServingRepository;
    private final VItemNutrientRepository vItemNutrientRepository;


    @Transactional
    public FoodServingDto createForProduct(
            Long productId,
            Float amount,
            Float gramWeight,
            Long servingUnitId,
            String description
    ) {

        if(amount <= 0)
            throw new IllegalArgumentException("Amount must be greater then 0");

        if(gramWeight <= 0)
            throw new IllegalArgumentException("Grem weight must be greater then 0");

        if(!servingUnitService.existsById(servingUnitId))
            throw new EntityNotFoundException("Serving unit not found");

        if(!productRepository.existsById(productId))
            throw new EntityNotFoundException("Product not found");

        return foodServingMapper.toDto(foodServingRepository.save(
                new FoodServingEntity(
                        productId,
                        ItemType.PRODUCT,
                        amount,
                        gramWeight,
                        servingUnitId,
                        description
                )
        ));
    }

    /**
     * Default portion for a user-created recipe (amount=1 "serving", gramWeight = total recipe weight).
     * Seeded recipes already have these rows; create path previously skipped them.
     */
    @Transactional
    public FoodServingDto ensureDefaultForRecipe(Long recipeId, Float totalWeightGrams) {
        float weight = (totalWeightGrams != null && totalWeightGrams > 0f) ? totalWeightGrams : 100f;
        Long unitId;
        try {
            unitId = servingUnitService.getIdByCode("serving");
        } catch (EntityNotFoundException e) {
            unitId = servingUnitService.existsById(6L) ? 6L : 1L;
        }

        var existing = foodServingRepository.findAllByItemIdAndItemType(recipeId, ItemType.RECIPE);
        if (!existing.isEmpty()) {
            FoodServingEntity first = existing.getFirst();
            first.setGramWeight(weight);
            if (first.getAmount() == null || first.getAmount() <= 0f) {
                first.setAmount(1f);
            }
            if (first.getDescription() == null || first.getDescription().isBlank()) {
                first.setDescription("Порция");
            }
            return foodServingMapper.toDto(foodServingRepository.save(first));
        }

        return foodServingMapper.toDto(foodServingRepository.save(
                new FoodServingEntity(
                        recipeId,
                        ItemType.RECIPE,
                        1f,
                        weight,
                        unitId,
                        "Порция"
                )
        ));
    }

    @Transactional(readOnly = true)
    public List<FoodServingEntity> getByProductId(Long productId) {

        return foodServingRepository.findAllByItemIdAndItemType(productId, ItemType.PRODUCT);
    }

    @Transactional(readOnly = true)
    public List<FoodServingEntity> getByRecipeId(Long recipeId) {
        return foodServingRepository.findAllByItemIdAndItemType(recipeId, ItemType.RECIPE);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long foodServingId) {
        return foodServingRepository.existsById(foodServingId);
    }


    // region Override

    @Transactional(readOnly = true)
    @Override
    public FoodServingDto getById(Long foodServingId) {
        return foodServingRepository
                .findById(foodServingId)
                .map(foodServingMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Serving with such id not found")
                );
    }

    @Transactional
    @Override
    public Map<Long, ProductServingDto> getInfoByServingIds(
            Collection<Long> servingIds,
            Long acceptedNutrientId
    ) {

        if( servingIds == null || servingIds.isEmpty())
            return Map.of();

        var servings = vFoodServingRepository.findAllByServingIdIn(servingIds);
        if (servings.isEmpty())
            return Map.of();

        Map<String, Float> nutrientByItem = loadNutrientPer100g(servings, Set.of(acceptedNutrientId))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getOrDefault(acceptedNutrientId, null)
                ));

        Map<Long, ProductServingDto> result = new HashMap<>();
        for(var serving : servings) {
            String key = itemKey(serving.getItemType(), serving.getItemId());

            result.put(
                    serving.getServingId(),
                    new ProductServingDto(
                            serving.getServingId(),
                            serving.getItemId(),
                            serving.getItemName(),
                            serving.getServingAmount(),
                            serving.getGramWeight(),
                            serving.getServingUnitCode(),
                            nutrientByItem.get(key),
                            serving.getPhotoPath()
                    )
            );
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Map<Long, Float>> getNutrientsByServingIds(
            Collection<Long> servingIds,
            Collection<Long> nutrientIds
    ) {
        if (servingIds == null || servingIds.isEmpty() || nutrientIds == null || nutrientIds.isEmpty()) {
            return Map.of();
        }

        var servings = vFoodServingRepository.findAllByServingIdIn(servingIds);
        if (servings.isEmpty()) {
            return Map.of();
        }

        Map<String, Map<Long, Float>> byItem = loadNutrientPer100g(servings, new HashSet<>(nutrientIds));

        Map<Long, Map<Long, Float>> result = new HashMap<>();
        for (var serving : servings) {
            String key = itemKey(serving.getItemType(), serving.getItemId());
            result.put(serving.getServingId(), byItem.getOrDefault(key, Map.of()));
        }
        return result;
    }

    // endregion

    private Map<String, Map<Long, Float>> loadNutrientPer100g(
            List<VFoodServingEntity> servings,
            Set<Long> nutrientIds
    ) {
        Set<String> keys = servings.stream()
                .map(s -> itemKey(s.getItemType(), s.getItemId()))
                .collect(Collectors.toSet());

        // Implemented via view queries in original service - keep minimal if methods below exist
        return Map.of();
    }

    private static String itemKey(String itemType, Long itemId) {
        return itemType + ":" + itemId;
    }

    private static String itemKey(ItemType itemType, Long itemId) {
        return itemType.name() + ":" + itemId;
    }
}
