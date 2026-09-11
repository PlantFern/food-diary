package com.github.plantfern.foodDiary.diaryProfiles.domain;


import com.github.plantfern.foodDiary.diaryProfiles.domain.dto.ProfileHiddenNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileHiddenNutrientEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileHiddenNutrientMapper {

    ProfileHiddenNutrientDto toDto(ProfileHiddenNutrientEntity profileHiddenNutrient);

    ProfileHiddenNutrientEntity toEntity(ProfileHiddenNutrientDto profileHiddenNutrient);

    List<ProfileHiddenNutrientDto> toDtoList(ProfileHiddenNutrientEntity profileHiddenNutrientList);
}
