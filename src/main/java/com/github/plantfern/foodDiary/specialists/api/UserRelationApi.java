package com.github.plantfern.foodDiary.specialists.api;

import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface UserRelationApi {
    List<UserRelationDto> findByDiaryProfileIdInternal(Long diaryProfileId);
    List<UserRelationDto> findBySpecialistIdInternal(Long specialistId);

    boolean existsByDiaryProfileIdAndSpecialistIdInternal
            (Long diaryProfileId,
             Long specialistId);
}
