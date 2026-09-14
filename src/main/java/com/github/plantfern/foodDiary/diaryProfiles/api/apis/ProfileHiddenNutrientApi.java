package com.github.plantfern.foodDiary.diaryProfiles.api.apis;

import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileHiddenNutrientEntity;

import java.util.List;

public interface ProfileHiddenNutrientApi {

    public List<ProfileHiddenNutrientEntity> getAllByProfileFeatureSettingsIdInternal(
            Long profileFeatureSettingsId);
}
