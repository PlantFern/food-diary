package com.github.plantfern.foodDiary.specialists.domain;


import com.github.plantfern.foodDiary.specialists.api.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationStatusEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserRelationMapper {
    UserRelationDto toDto(UserRelationEntity userRelationEntity);

    UserRelationEntity toEntity(UserRelationDto userRelationDto);

    List<UserRelationDto> toListDto(List<UserRelationStatusEntity> userRelationStatusEntityList);
}
