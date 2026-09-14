package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalNutrientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GoalNutrientRepository extends JpaRepository<GoalNutrientEntity, Long> {

    List<GoalNutrientEntity> findByGoalId(Long goalId);
}
