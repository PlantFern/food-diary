package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.api.dto.ProductNutrientItemDto;
import com.github.plantfern.foodDiary.food.domain.views.VProductNutrientEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface VProductNutrientRepository extends JpaRepository<VProductNutrientEntity, Long> {

    @Query("""
    select new com.github.plantfern.foodDiary.food.api.dto.ProductNutrientItemDto(
        vProductNutrient.nutrientId,
        vProductNutrient.nutrientCode,
        vProductNutrient.amountPer100g,
        vProductNutrient.unitCode
    )
    from VProductNutrientEntity vProductNutrient
    where vProductNutrient.productId = :productId
      and ( :hiddenNutrients is null
          or vProductNutrient.nutrientId not in :hiddenNutrients)
    """)
    List<ProductNutrientItemDto> findPersonalizedByProductId(
            @Param("productId") Long productId,
            @Param("acceptedNutrients") @Nullable Set<Long> hiddenNutrients
    );

    @Query("""
    select new com.github.plantfern.foodDiary.food.api.dto.ProductNutrientItemDto(
        vProductNutrient.nutrientId,
        vProductNutrient.nutrientCode,
        vProductNutrient.amountPer100g,
        vProductNutrient.unitCode
    )
    from VProductNutrientEntity vProductNutrient
    where vProductNutrient.productId = :productId
    """)
    List<ProductNutrientItemDto> findByProductId(
            Long productId
    );
}
