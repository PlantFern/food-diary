package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface GoalRepository extends JpaRepository<GoalEntity, Long> {

    Optional<GoalEntity> findFirstByDiaryProfileIdOrderByCreatedAtDesc(Long diaryProfileId);
}
