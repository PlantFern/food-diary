package com.github.plantfern.foodDiary.meals.domain.repositories;


import com.github.plantfern.foodDiary.meals.domain.entities.MealEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<MealEntity, Long> {

    List<MealEntity> findAllByDiaryProfileIdAndCreatedAtBetween(
            Long diaryProfileId,
            LocalDateTime createdAt,
            LocalDateTime createdAt2
    );
}
