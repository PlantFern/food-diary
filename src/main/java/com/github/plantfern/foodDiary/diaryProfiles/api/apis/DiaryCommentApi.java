package com.github.plantfern.foodDiary.diaryProfiles.api.apis;

import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryCommentDto;

import java.time.LocalDate;
import java.util.List;

public interface DiaryCommentApi {

    List<DiaryCommentDto> getByDiaryProfileAndCommentableTypeInternal
            (Long diaryProfileId, CommentableType type);

    List<DiaryCommentDto> getByDiaryProfileAndCommentDateInternal
            (Long diaryProfileId, LocalDate commentDate);

    DiaryCommentDto getByIdInternal(Long id);
}
