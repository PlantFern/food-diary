package com.github.plantfern.foodDiary.common.repositories;


import com.github.plantfern.foodDiary.common.entities.NutrientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NutrientRepository extends JpaRepository<NutrientEntity, Long> {
}
