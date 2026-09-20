package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryCommentApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryCommentDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryCommentEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.DiaryCommentMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.DiaryCommentRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@Service
public class DiaryCommentService implements DiaryCommentApi {

    private final DiaryCommentRepository diaryCommentRepository;
    private final DiaryCommentMapper diaryCommentMapper;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;
    private final DiaryProfileService diaryProfileService;


    @Transactional
    public void create(
            Long diaryProfileId,
            CommentableType commentableType,
            Long commentableId,
            String body
    ) {

        var foundDiaryProfile = diaryProfileService
                .getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureHasRelationsWithProfile(currentUser, foundDiaryProfile.userId());

        diaryCommentRepository.save(
                new DiaryCommentEntity(
                        foundDiaryProfile.id(),
                        commentableType,
                        commentableId,
                        body,
                        currentUser.requireId()
                )
        );
    }

    @Transactional
    public void update(
            Long diaryCommentId,
            Long diaryProfileId,
            CommentableType commentableType,
            Long commentableId,
            String body
    ) {

        var foundDiaryComment = diaryCommentRepository
                .findById(diaryCommentId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Diary comment with such id not found")
                );

        diaryProfilePolicy.ensureCreatedBy(currentUser, foundDiaryComment.getCreatedById());

        foundDiaryComment.setDiaryProfileId(diaryProfileId);
        foundDiaryComment.setCommentableType(commentableType);
        foundDiaryComment.setCommentableId(commentableId);
        foundDiaryComment.setBody(body);

        diaryCommentRepository.save(
                foundDiaryComment
        );
    }

    @Transactional
    public void delete(
            Long diaryCommentId
    ) {
        var foundDiaryComment = diaryCommentRepository
                .findById(diaryCommentId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Diary comment with such id not found")
                );

        diaryProfilePolicy.ensureCreatedBy(currentUser, foundDiaryComment.getCreatedById());

        diaryCommentRepository.delete(foundDiaryComment);
    }


    @Transactional(readOnly = true)
    public List<DiaryCommentDto> getByDiaryProfileAndCommentableType(Long diaryProfileId, CommentableType type) {
        var foundDiaryProfile = diaryProfileService
                .getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureHasRelationsWithProfile(currentUser, foundDiaryProfile.userId());

        return this.getByDiaryProfileAndCommentableTypeInternal(diaryProfileId, type);
    }

    @Transactional
    public DiaryCommentDto getById(Long id) {

        var diaryComment = this.getByIdInternal(id);

        var foundDiaryProfile = diaryProfileService
                .getByIdInternal(diaryComment.diaryProfileId());

        diaryProfilePolicy.ensureHasRelationsWithProfile(currentUser, foundDiaryProfile.userId());

        return diaryComment;
    }


    @Override
    @Transactional(readOnly = true)
    public List<DiaryCommentDto> getByDiaryProfileAndCommentableTypeInternal(Long diaryProfileId, CommentableType type) {
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
    public DiaryCommentDto getByIdInternal(Long id) {

        return diaryCommentRepository
                .findById( id
                )
                .map(diaryCommentMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Diary comment with such id not found")
                );
    }
}
