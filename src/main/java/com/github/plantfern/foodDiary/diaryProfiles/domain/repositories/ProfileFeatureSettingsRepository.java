package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileFeatureSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProfileFeatureSettingsRepository extends JpaRepository<ProfileFeatureSettingsEntity, Long> {

    Optional<ProfileFeatureSettingsEntity> findFirstByDiaryProfileIdOrderByCreatedAtDesc(Long diaryProfileId);

    List<ProfileFeatureSettingsEntity> findAllByDiaryProfileId(Long diaryProfileId);

    List<Long> findAllDiaryProfileIdByCreatedById(Long userId);

    List<ProfileFeatureSettingsEntity> findAllByCreatedById(Long userId);

    List<ProfileFeatureSettingsEntity> findAllByCreatedByIdAndDiaryProfileId(Long userId, Long diaryProfileId);

    List<ProfileFeatureSettingsEntity> findAllByExpiredAtGreaterThanEqualOrCreatedAtLessThanAndDiaryProfileId(
            LocalDateTime expiredAt,
            LocalDateTime createdAt,
            Long diaryProfileId);
}
