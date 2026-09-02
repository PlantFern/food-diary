package com.github.plantfern.foodDiary.diaryProfiles.domain;


import com.github.plantfern.foodDiary.diaryProfiles.domain.dto.DiaryProfileDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring")
public interface DiaryProfileMapper {

    DiaryProfileDto toDto(DiaryProfileEntity diaryProfileEntity);

    DiaryProfileEntity toEntity(DiaryProfileDto diaryProfileDto);

    List<DiaryProfileDto> toDtoList(List<DiaryProfileEntity> diaryProfileEntityList);
}
