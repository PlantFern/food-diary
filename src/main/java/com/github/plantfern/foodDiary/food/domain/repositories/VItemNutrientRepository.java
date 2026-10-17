package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.domain.views.VItemNutrientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface VItemNutrientRepository extends JpaRepository<VItemNutrientEntity, Long> {

    List<VItemNutrientEntity> findAllByItemTypeAndItemIdAndNutrientId(
            String itemType,
            Long itemId,
            Long nutrientId
    );

    /**
     * Explicit COLLATE avoids "Illegal mix of collations" between
     * connection default (utf8mb4_0900_ai_ci) and view column (utf8mb4_unicode_ci).
     */
    @Query(
            value = """
                    SELECT itemId, itemType, nutrientId, nutrientCode, amountPer100g, unitCode
                    FROM v_item_nutrients
                    WHERE itemType = CONVERT(:itemType USING utf8mb4) COLLATE utf8mb4_unicode_ci
                      AND itemId IN (:itemIds)
                      AND nutrientId IN (:nutrientIds)
                    """,
            nativeQuery = true
    )
    List<VItemNutrientEntity> findAllByItemTypeAndItemIdInAndNutrientIdIn(
            @Param("itemType") String itemType,
            @Param("itemIds") Collection<Long> itemIds,
            @Param("nutrientIds") Collection<Long> nutrientIds
    );
}
