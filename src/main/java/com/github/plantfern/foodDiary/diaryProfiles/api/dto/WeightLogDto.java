package com.github.plantfern.foodDiary.diaryProfiles.api.dto;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;

public record WeightLogDto(
        DiaryProfileEntity diaryProfile,
        Float weight
) { }
