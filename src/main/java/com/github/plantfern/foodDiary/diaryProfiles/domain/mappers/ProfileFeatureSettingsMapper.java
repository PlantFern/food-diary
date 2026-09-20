package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.ProfileFeatureSettingsDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileFeatureSettingsEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileHiddenNutrientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;


@Mapper(componentModel = "spring")
public interface ProfileFeatureSettingsMapper {

    @Mapping(
            target = "hiddenNutrientIds",
            source = "profileHiddenNutrientSet"
    )
    ProfileFeatureSettingsDto toDto(ProfileFeatureSettingsEntity profileFeatureSettings);

    @Mapping(target="profileHiddenNutrientSet", ignore=true)
    ProfileFeatureSettingsEntity toEntity(ProfileFeatureSettingsDto profileFeatureSettings);

    Set<ProfileFeatureSettingsDto> toDtoList(Set<ProfileFeatureSettingsEntity> profileFeatureSettingsSet);

    default Long mapHiddenNutrient(ProfileHiddenNutrientEntity entity){
        return entity.getNutrientId();
    }
}
