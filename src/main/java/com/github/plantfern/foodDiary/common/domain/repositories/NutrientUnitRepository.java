package com.github.plantfern.foodDiary.common.domain.repositories;


import com.github.plantfern.foodDiary.common.domain.entities.NutrientUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NutrientUnitRepository extends JpaRepository<NutrientUnitEntity, Long> {
}
