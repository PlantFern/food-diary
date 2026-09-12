package com.github.plantfern.foodDiary.diaryProfiles.api;


import com.github.plantfern.foodDiary.diaryProfiles.domain.dto.ProfileFeatureSettingsDto;

public interface ProfileFeatureSettingsApi {
    ProfileFeatureSettingsDto getByIdInternal(Long id);
}
