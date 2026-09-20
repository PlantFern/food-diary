package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.domain.entities.ProductRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRequestRepository extends JpaRepository<ProductRequestEntity, Long> {

    boolean existsByProductId(Long productId);
}
