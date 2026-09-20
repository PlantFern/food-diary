package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;

import java.time.LocalDate;

public record CommentRequest(
        CommentableType commentableType,
        Long commentableId,
        String body,
        LocalDate commentDate
) {
}
