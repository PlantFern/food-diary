package com.github.plantfern.foodDiary.diaryProfiles.api.dto;


import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;

import java.time.LocalDate;


public record DiaryCommentDto (
        Long id,
        Long diaryProfileId,
        CommentableType commentableType,
        Long commentableId,
        String body,
        LocalDate commentDate,
        Long createdById
) {
}
