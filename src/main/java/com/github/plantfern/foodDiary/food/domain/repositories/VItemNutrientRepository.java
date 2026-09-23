package com.github.plantfern.foodDiary.food.domain.repositories;

import com.github.plantfern.foodDiary.food.domain.views.VItemNutrientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface VItemNutrientRepository extends JpaRepository<VItemNutrientEntity, Long> {

    List<VItemNutrientEntity> findAllByItemTypeAndItemIdAndNutrientId(
            String itemType,
            Long itemId,
            Long nutrientId
    );

    List<VItemNutrientEntity> findAllByItemTypeAndItemIdAndNutrientIdIn(
            String itemType,
            Long itemId,
            Collection<Long> nutrientId
    );
}
