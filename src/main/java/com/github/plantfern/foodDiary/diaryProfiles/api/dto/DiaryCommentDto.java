package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;

import java.time.LocalDate;

public record DiaryCommentDto (
        Long diaryProfileId,
        CommentableType commentableType,
        Long commentableId,
        String body,
        LocalDate commentDate,
        Long createdById
) {
}
