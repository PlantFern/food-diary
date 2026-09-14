package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryCommentDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryCommentEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface DiaryCommentMapper {

    DiaryCommentDto toDto(DiaryCommentEntity diaryCommentEntity);

    DiaryCommentEntity toEntity(DiaryCommentDto diaryCommentDto);

    List<DiaryCommentDto> toDtoList(List<DiaryCommentEntity> diaryCommentEntityList);
}
