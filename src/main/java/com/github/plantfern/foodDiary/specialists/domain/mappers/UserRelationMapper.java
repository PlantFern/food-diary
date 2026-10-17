package com.github.plantfern.foodDiary.specialists.domain.mappers;


import com.github.plantfern.foodDiary.specialists.api.UserRelationStatus;
import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserRelationMapper {

    @Mapping(target = "relationStatusCode", source = ".", qualifiedByName = "statusCode")
    UserRelationDto toDto(UserRelationEntity userRelationEntity);

    @Mapping(target = "userRelationStatusEntity", ignore = true)
    @Mapping(target = "specialist", ignore = true)
    UserRelationEntity toEntity(UserRelationDto userRelationDto);

    List<UserRelationDto> toListDto(List<UserRelationStatusEntity> userRelationStatusEntityList);

    @Named("statusCode")
    default UserRelationStatus statusCode(UserRelationEntity entity) {
        if (entity == null) return null;
        try {
            return entity.getStatusCode();
        } catch (Exception e) {
            var status = entity.getUserRelationStatusEntity();
            if (status == null || status.getCode() == null) return null;
            return UserRelationStatus.valueOf(status.getCode());
        }
    }
}
