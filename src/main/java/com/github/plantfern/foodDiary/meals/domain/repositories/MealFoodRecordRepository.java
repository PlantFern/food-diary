package com.github.plantfern.foodDiary.meals.domain.repositories;


import com.github.plantfern.foodDiary.meals.domain.entities.MealFoodRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MealFoodRecordRepository extends JpaRepository<MealFoodRecordEntity, Long> {

    List<MealFoodRecordEntity> findAllByMealId(Long mealId);

    /**
     * Delete the row directly instead of scheduling deletion of a managed entity.
     * This avoids Hibernate's stale-state exception when two requests try to remove
     * the same record concurrently. A zero row count means another request already
     * removed the row after this transaction loaded it.
     */
    @Modifying
    @Query(value = "DELETE FROM food_records WHERE id = :recordId", nativeQuery = true)
    int deleteRecordRowById(@Param("recordId") Long recordId);
}
