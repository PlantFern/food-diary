package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.domain.entities.BrandedProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BrandedProductRepository extends JpaRepository<BrandedProductEntity, Long> {

    boolean existsByBarcodeAndDeletedAtIsNull(String barcode);

    Optional<BrandedProductEntity> findByBarcodeAndDeletedAtIsNull(String barcode);
}
