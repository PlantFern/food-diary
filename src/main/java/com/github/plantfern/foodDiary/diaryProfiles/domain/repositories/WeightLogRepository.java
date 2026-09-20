package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.WeightLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface WeightLogRepository extends JpaRepository<WeightLogEntity, Long> {

    Optional<WeightLogEntity> findFirstByDiaryProfileIdOrderByCreatedAtDesc(Long diaryProfileId);

    List<WeightLogEntity> findAllByDiaryProfileIdAndCreatedAtBetween(
            Long diaryProfileId,
            LocalDateTime beginPeriod,
            LocalDateTime endPeriod);

    List<WeightLogEntity> findByDiaryProfileId(Long diaryProfileId);
}
