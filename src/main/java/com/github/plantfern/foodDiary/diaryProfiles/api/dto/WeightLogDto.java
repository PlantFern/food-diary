package com.github.plantfern.foodDiary.diaryProfiles.api.dto;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;

public record WeightLogDto(
        Long diaryProfileId,
        Float weight
) { }
