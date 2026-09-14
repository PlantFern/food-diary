package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalNutrientEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface GoalNutrientMapper {

    GoalNutrientDto toDto(GoalNutrientEntity goalNutrientEntity);

    GoalNutrientEntity toEntity(GoalNutrientDto goalNutrientDto);

    List<GoalNutrientDto> toDtoList(List<GoalNutrientEntity> goalNutrientEntityList);
}
