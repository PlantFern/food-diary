package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileHiddenNutrientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProfileHiddenNutrientRepository extends JpaRepository<ProfileHiddenNutrientEntity, Long> {

    List<ProfileHiddenNutrientEntity> findAllByProfileFeatureSettingsId(Long profileFeatureSettingsId);

    Optional<ProfileHiddenNutrientEntity> findFirstByProfileFeatureSettingsIdAndNutrientId
            (Long profileFeatureSettings_id, Long nutrientId);
}
