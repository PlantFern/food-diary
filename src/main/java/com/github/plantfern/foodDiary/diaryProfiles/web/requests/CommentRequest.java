package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CommentRequest(
        @NotNull CommentableType commentableType,
        @NotNull Long commentableId,
        @Size(max = 2000) @NotBlank String body
) {
}
