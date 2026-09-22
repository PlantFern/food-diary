package com.github.plantfern.foodDiary.meals.domain.repositories;


import com.github.plantfern.foodDiary.meals.domain.entities.MealTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MealTemplateRepository extends JpaRepository<MealTemplateEntity, Long> {

    List<MealTemplateEntity> findAllByDiaryProfileId(Long profileId);
}
