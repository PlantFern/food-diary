package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.SleepLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface SleepLogRepository extends JpaRepository<SleepLogEntity, Long> {

    Optional<SleepLogEntity> findAllByDiaryProfileIdAndBeganAtIsBeforeAndEndedAtIsAfter
            (Long diaryProfileId, LocalDateTime beganAt, LocalDateTime endedAt);

    List<SleepLogEntity> findAllByDiaryProfileIdAndBeganAtLessThanEqualAndEndedAtGreaterThanEqual(
            Long diaryProfileId,
            LocalDateTime beganAt,
            LocalDateTime endedAt
    );

    List<SleepLogEntity> findByDiaryProfileId(Long diaryProfileId);
}
