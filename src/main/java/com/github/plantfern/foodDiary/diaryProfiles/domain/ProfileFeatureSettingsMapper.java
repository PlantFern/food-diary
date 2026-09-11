package com.github.plantfern.foodDiary.diaryProfiles.domain;


import com.github.plantfern.foodDiary.diaryProfiles.domain.dto.ProfileFeatureSettingsDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileFeatureSettingsEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ProfileFeatureSettingsMapper {

    ProfileFeatureSettingsDto toDto(ProfileFeatureSettingsEntity profileFeatureSettings);

    ProfileFeatureSettingsEntity toEntity(ProfileFeatureSettingsDto profileFeatureSettings);

    List<ProfileFeatureSettingsDto> toDtoList(List<ProfileFeatureSettingsEntity> profileFeatureSettingsList);
}
