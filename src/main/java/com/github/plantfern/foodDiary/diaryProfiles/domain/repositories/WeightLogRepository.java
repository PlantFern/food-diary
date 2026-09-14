package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.WeightLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.WatchEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface WeightLogRepository extends JpaRepository<WeightLogEntity, Long> {

    Optional<WeightLogEntity> findFirstByDiaryProfileIdOrderByCreatedAtDesc(Long diaryProfileId);

    List<WeightLogEntity> findByDiaryProfileId(Long diaryProfileId);
}
