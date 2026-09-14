package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.WeightLogEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface WeightLogMapper {

    WeightLogDto toDto(WeightLogEntity weightLogEntity);

    WeightLogEntity toEntity(WeightLogDto weightLogDto);

    List<WeightLogDto> toDtoList(List<WeightLogEntity> weightLogEntityList);
}
