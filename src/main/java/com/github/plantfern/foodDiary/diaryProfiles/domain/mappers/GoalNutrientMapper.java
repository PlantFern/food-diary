package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalNutrientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface GoalNutrientMapper {

    GoalNutrientDto toDto(GoalNutrientEntity goalNutrientEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "goalId", ignore = true)
    @Mapping(target = "nutrientId", ignore = true)
    @Mapping(target = "amount", ignore = true)
    GoalNutrientEntity toEntity(GoalNutrientDto goalNutrientDto);

    List<GoalNutrientDto> toDtoList(List<GoalNutrientEntity> goalNutrientEntityList);
}
