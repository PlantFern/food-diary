package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryCommentApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryCommentDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.DiaryCommentMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.DiaryCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@Service
public class DiaryCommentService implements DiaryCommentApi {

    private final DiaryCommentRepository diaryCommentRepository;
    private final DiaryCommentMapper diaryCommentMapper;


    @Override
    public List<DiaryCommentDto> getByDiaryProfileAndCommentableType(Long diaryProfileId, CommentableType type) {
        return diaryCommentRepository
                .findAllByCommentableTypeAndDiaryProfileId(
                        type,
                        diaryProfileId
                )
                .stream()
                .map(diaryCommentMapper::toDto)
                .toList();
    }

    @Override
    public List<DiaryCommentDto> getByDiaryProfileAndCommentDate(Long diaryProfileId, LocalDate commentDate) {
        return diaryCommentRepository
                .findAllByDiaryProfileIdAndCommentDate(
                        diaryProfileId,
                        commentDate
                )
                .stream()
                .map(diaryCommentMapper::toDto)
                .toList();
    }

    @Override
    public DiaryCommentDto getById(Long id) {
        return diaryCommentRepository
                .findById( id
                )
                .map(diaryCommentMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Diary comment with such id not found")
                );
    }
}
