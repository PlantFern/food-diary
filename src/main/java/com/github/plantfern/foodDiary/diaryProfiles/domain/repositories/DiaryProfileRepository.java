package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;

import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DiaryProfileRepository extends JpaRepository<DiaryProfileEntity, Long> {
    boolean existsByUserId(Long userId);
    Optional<Long> findByUserId(Long userId);
}
