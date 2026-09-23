package com.github.plantfern.foodDiary.food.domain.services;

import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.api.apis.FoodServingApi;
import com.github.plantfern.foodDiary.food.api.dto.FoodServingDto;
import com.github.plantfern.foodDiary.food.api.dto.ProductServingDto;
import com.github.plantfern.foodDiary.food.domain.entities.FoodServingEntity;
import com.github.plantfern.foodDiary.food.domain.entities.ProductEntity;
import com.github.plantfern.foodDiary.food.domain.entities.ServingUnitEntity;
import com.github.plantfern.foodDiary.food.domain.mappers.FoodServingMapper;
import com.github.plantfern.foodDiary.food.domain.repositories.*;
import com.github.plantfern.foodDiary.food.domain.views.VFoodServingEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class FoodServingService implements FoodServingApi {

    private final FoodServingRepository foodServingRepository;
    private final ProductService productService;
    private final ServingUnitService servingUnitService;
    private final FoodServingMapper foodServingMapper;
    private final ProductRepository productRepository;
    private final ProductNutrientRepository productNutrientRepository;
    private final ServingUnitRepository servingUnitRepository;
    private final VFoodServingRepository vFoodServingRepository;
    private final VItemNutrientRepository vItemNutrientRepository;

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

        if(!productService.existsById(productId))
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

    public List<FoodServingEntity> getByProductId(Long productId) {

        return foodServingRepository.findAllByItemIdAndItemType(productId, ItemType.PRODUCT);
    }

    public boolean existsById(Long foodServingId) {
        return foodServingRepository.existsById(foodServingId);
    }


    // region Override

    @Override
    public FoodServingDto getById(Long foodServingId) {
        return foodServingRepository
                .findById(foodServingId)
                .map(foodServingMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Serving with such id not found")
                );
    }

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
            String key = serving.getItemType() + ":" + serving.getItemId();

            result.put(
                    serving.getServingId(),
                    new ProductServingDto(
                            serving.getServingId(),
                            serving.getItemId(),
                            serving.getItemName(),
                            serving.getServingAmount(),
                            serving.getGramWeight(),
                            serving.getServingUnitCode(),
                            nutrientByItem.get(key)
                    )
            );
        }

        return result;
    }

    @Override
    public Map<Long, Map<Long, Float>> getNutrientsByServingIds(
            Collection<Long> servingIds,
            Collection<Long> nutrientIds
    ) {

        if(servingIds == null || servingIds.isEmpty())
            return Map.of();

        var servings = vFoodServingRepository.findAllByServingIdIn(servingIds);
        Map<String, Map<Long, Float>> byItem = loadNutrientPer100g(servings, nutrientIds);

        Map<Long, Map<Long, Float>> result = new HashMap<>();
        for(var serving : servings){
            result.put(
                    serving.getServingId(),
                    byItem.getOrDefault(serving.getItemType() + ":" + serving.getItemId(), Map.of())
            );
        }

        return result;
    }
    // endregion

    // region Private methods

    private Map<String, Map<Long, Float>> loadNutrientPer100g(
            List<VFoodServingEntity> servings,
            Collection<Long> nutrientIds
    ) {

        Map<String, Set<Long>> idsByType = new HashMap<>();

        for(var serving : servings) {
            idsByType.computeIfAbsent(
                    serving.getItemType(), t -> new HashSet<>()).add(serving.getItemId()
            );
        }

        Map<String, Map<Long, Float>> result = new HashMap<>();

        for (var entry : idsByType.entrySet()) {
            var rows = vItemNutrientRepository.findAllByItemTypeAndItemIdInAndNutrientIdIn(
                    entry.getKey(),
                    entry.getValue(),
                    nutrientIds
            );
            for (var nutrient: rows) {
                result
                        .computeIfAbsent(
                                nutrient.getItemType() + " : " +  nutrient.getItemId(),
                                key -> new HashMap<>()
                        )
                        .put(nutrient.getNutrientId(), nutrient.getAmountPer100g());
            }
        }

        return result;
    }
    // endregion
}
