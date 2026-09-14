package com.github.plantfern.foodDiary.diaryProfiles.api;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryProfileDto;

public interface DiaryProfileApi {
    DiaryProfileDto findByIdInternal(Long id);
    boolean existsByIdInternal(Long id);

    Long getOwnerUserIdInternal(Long diaryProfileId);
}
