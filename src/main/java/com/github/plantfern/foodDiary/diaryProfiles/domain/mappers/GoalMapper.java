package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryCommentDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryCommentEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface GoalMapper {

    GoalDto toDto(GoalEntity goalEntity);

    GoalEntity toEntity(GoalDto goalDto);

    List<GoalDto> toDtoList(List<GoalEntity> goalEntityList);
}
