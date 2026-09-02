package com.github.plantfern.foodDiary.specialists.domain.repositories;


import com.github.plantfern.foodDiary.specialists.domain.entities.SpecialistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SpecialistRepository extends JpaRepository<SpecialistEntity, Long> {
    SpecialistEntity findByUserId(Long userId);
}
