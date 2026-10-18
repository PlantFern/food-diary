package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.domain.entities.ServingUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ServingUnitRepository extends JpaRepository<ServingUnitEntity, Long> {

    Optional<ServingUnitEntity> findByCode(String code);
}
