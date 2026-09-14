package com.github.plantfern.foodDiary.diaryProfiles.api.apis;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.ProfileFeatureSettingsDto;

public interface ProfileFeatureSettingsApi {
    ProfileFeatureSettingsDto getByIdInternal(Long id);
}
