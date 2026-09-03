package com.github.plantfern.foodDiary.specialists.api;

import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface UserRelationApi {
    List<UserRelationDto> findByDiaryProfileId(Long targetId);
    List<UserRelationDto> findBySpecialistId(Long targetId);

    List<UserRelationDto> findAll();
}
