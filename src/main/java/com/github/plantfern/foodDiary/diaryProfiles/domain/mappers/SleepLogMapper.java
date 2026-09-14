package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.SleepLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.SleepLogEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface SleepLogMapper {

    SleepLogDto toDto(SleepLogEntity sleepLogEntity);

    SleepLogEntity toEntity(SleepLogDto sleepLogDto);

    List<SleepLogDto> toDtoList(List<SleepLogEntity> sleepLogEntityList);
}
