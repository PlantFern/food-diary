package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;


import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryCommentEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface DiaryCommentRepository extends JpaRepository<DiaryCommentEntity, Long> {

    List<DiaryCommentEntity> findAllByCommentableTypeAndDiaryProfileId
            (CommentableType commentableType, Long diaryProfile);

    List<DiaryCommentEntity> findAllByDiaryProfileAndCommentableTypeOrderByCommentDateDesc(
            DiaryProfileEntity diaryProfile, CommentableType commentableType
    );

    List<DiaryCommentEntity> findAllByDiaryProfileIdAndCommentDate
            (Long diaryProfileId, LocalDate commentDate);
}
