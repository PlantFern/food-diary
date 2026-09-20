package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(
        componentModel = "spring",
        uses = GoalNutrientMapper.class
)
public interface GoalMapper {

    @Mapping(
            target = "nutrientSet",
            source = "goalNutrientSet"
    )
    GoalDto toDto(GoalEntity goalEntity);

    @Mapping(target = "goalNutrientSet", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "plannedEndDate", ignore = true)
    @Mapping(target = "actualEndDate", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "plannedWeight", ignore = true)
    @Mapping(target = "diaryProfileId", ignore = true)
    GoalEntity toEntity(GoalDto goalDto);

    List<GoalDto> toDtoList(List<GoalEntity> goalEntityList);
}
