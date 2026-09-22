package com.github.plantfern.foodDiary.meals.domain.repositories;


import com.github.plantfern.foodDiary.meals.domain.entities.MealTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MealTypeRepository extends JpaRepository<MealTypeEntity, Long> {

    Optional<MealTypeEntity> findByCode(String code);
}
