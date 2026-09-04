package com.github.plantfern.foodDiary.specialists.api;

import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface UserRelationApi {
    List<UserRelationDto> findByDiaryProfileId(Long diaryProfileId);
    List<UserRelationDto> findBySpecialistId(Long specialistId);

    List<UserRelationDto> findAll();

    boolean existsByDiaryProfileIdAndSpecialistId(Long diaryProfileId, Long specialistId);
}
