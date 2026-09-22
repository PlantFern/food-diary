package com.github.plantfern.foodDiary.meals.domain.repositories;


import com.github.plantfern.foodDiary.meals.domain.entities.MealEntity;
import com.github.plantfern.foodDiary.meals.domain.entities.MealTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<MealEntity, Long> {

    List<MealEntity> findAllByDiaryProfileIdAndDate(
            Long diaryProfileId, LocalDate date
    );

    Optional<MealEntity> findAllByDiaryProfileIdAndMealTypeIdAndDate(
            Long diaryProfileId, Long mealTypeId, LocalDate date
    );
}
