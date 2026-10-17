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
                .orElseThrow(() -> new EntityNotFoundException("Food serving not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public Map<Long, ProductServingDto> getInfoByServingIds(
            Collection<Long> servingIds,
            Long acceptedNutrientId
    ) {
        if (servingIds == null || servingIds.isEmpty()) {
            return Map.of();
        }

        var servings = vFoodServingRepository.findAllByServingIdIn(servingIds);
        if (servings.isEmpty())
            return Map.of();

        Map<String, Float> nutrientByItem = loadNutrientPer100g(servings, Set.of(acceptedNutrientId))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getOrDefault(acceptedNutrientId, 0f)
                ));

        Map<Long, ProductServingDto> result = new HashMap<>();
        for(var serving : servings) {
            String key = serving.getItemType() + ":" + serving.getItemId();
            result.put(
                    serving.getServingId(),
                    new ProductServingDto(
                            serving.getServingId(),
                            serving.getItemId(),
                            serving.getItemType() != null ? serving.getItemType().name() : null,
                            serving.getProductDescription(),
                            serving.getServingAmount(),
                            serving.getGramWeight(),
                            serving.getServingUnitCode(),
                            nutrientByItem.get(key)
                    )
            );
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<Long, Map<Long, Float>> getNutrientsByServingIds(
            Collection<Long> servingIds,
            Collection<Long> nutrientIds
    ) {
        if (servingIds == null || servingIds.isEmpty() || nutrientIds == null || nutrientIds.isEmpty()) {
            return Map.of();
        }

        var servings = vFoodServingRepository.findAllByServingIdIn(servingIds);
        Map<String, Map<Long, Float>> byItem = loadNutrientPer100g(servings, nutrientIds);

        Map<Long, Map<Long, Float>> result = new HashMap<>();
        for(var serving : servings){
            String key = serving.getItemType() + ":" + serving.getItemId();
            Map<Long, Float> per100 = byItem.getOrDefault(key, Map.of());
            float factor = 1f;
            if (serving.getGramWeight() != null && serving.getServingAmount() != null) {
                factor = serving.getGramWeight() * serving.getServingAmount() / 100f;
            } else if (serving.getGramWeight() != null) {
                factor = serving.getGramWeight() / 100f;
            }
            Map<Long, Float> scaled = new HashMap<>();
            for (var e : per100.entrySet()) {
                scaled.put(e.getKey(), e.getValue() * factor);
            }
            result.put(serving.getServingId(), scaled);
        }
        return result;
    }

    private Map<String, Map<Long, Float>> loadNutrientPer100g(
            List<VFoodServingEntity> servings,
            Collection<Long> nutrientIds
    ) {
        Map<String, Map<Long, Float>> result = new HashMap<>();
        if (servings.isEmpty() || nutrientIds == null || nutrientIds.isEmpty()) {
            return result;
        }

        for(var serving : servings) {
            String key = serving.getItemType() + ":" + serving.getItemId();
            result.putIfAbsent(key, new HashMap<>());
        }

        // Batch by item keys from view nutrients
        for (var serving : servings) {
            var rows = vItemNutrientRepository.findByItemTypeAndItemId(
                    serving.getItemType(),
                    serving.getItemId()
            );
            String key = serving.getItemType() + ":" + serving.getItemId();
            Map<Long, Float> map = result.get(key);
            for (var row : rows) {
                if (nutrientIds.contains(row.getNutrientId())) {
                    map.put(row.getNutrientId(), row.getAmount() != null ? row.getAmount() : 0f);
                }
            }
        }
        return result;
    }

    // endregion
}
