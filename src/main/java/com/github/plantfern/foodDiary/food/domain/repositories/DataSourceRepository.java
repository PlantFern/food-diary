package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.domain.entities.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DataSourceRepository extends JpaRepository<DataSourceEntity, Long> {

    Optional<DataSourceEntity> findByCode(String code);
}
