package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.ProfileFeatureSettingsDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileFeatureSettingsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProfileFeatureSettingsMapper {

    @Mapping(
            target = "hiddenNutrientIds",
            source = "profileHiddentNutrientEntityList"
    )
    ProfileFeatureSettingsDto toDto(ProfileFeatureSettingsEntity profileFeatureSettings);

    @Mapping(target="profileHiddentNutrientEntityList", ignore=true)
    ProfileFeatureSettingsEntity toEntity(ProfileFeatureSettingsDto profileFeatureSettings);

    List<ProfileFeatureSettingsDto> toDtoList(List<ProfileFeatureSettingsEntity> profileFeatureSettingsList);
}
