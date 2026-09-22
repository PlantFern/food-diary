package com.github.plantfern.foodDiary.meals.domain.repositories;


import com.github.plantfern.foodDiary.meals.domain.entities.MealFoodRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MealFoodRecordRepository extends JpaRepository<MealFoodRecordEntity, Long> {

    List<MealFoodRecordEntity> findAllByMealId(Long mealId);
}
