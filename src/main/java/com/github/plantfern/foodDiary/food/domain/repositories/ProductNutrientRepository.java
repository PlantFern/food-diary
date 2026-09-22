package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.domain.entities.ProductNutrientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface ProductNutrientRepository extends JpaRepository<ProductNutrientEntity, Long> {

    List<ProductNutrientEntity> findAllByProductIdInAndNutrientId(
            Collection<Long> productId,
            Long nutrientId
    );
}
