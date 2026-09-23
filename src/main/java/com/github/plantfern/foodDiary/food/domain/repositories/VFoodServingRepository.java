package com.github.plantfern.foodDiary.food.domain.repositories;

import com.github.plantfern.foodDiary.food.domain.views.VFoodServingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface VFoodServingRepository extends JpaRepository<VFoodServingEntity, Long> {

    List<VFoodServingEntity> findAllByServingIdIn(
            Collection<Long> servingIds
    );
}
