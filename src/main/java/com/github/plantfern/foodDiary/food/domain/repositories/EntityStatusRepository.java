package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.domain.entities.EntityStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EntityStatusRepository extends JpaRepository<EntityStatusEntity, Long> {

    Optional<EntityStatusEntity> findByCode(String code);
}
