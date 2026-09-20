package com.github.plantfern.foodDiary.common.repositories;


import com.github.plantfern.foodDiary.common.entities.NutrientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;


@Repository
public interface NutrientRepository extends JpaRepository<NutrientEntity, Long> {

    Long countByIdIn(@Param("ids") Collection<Long> ids);
}
