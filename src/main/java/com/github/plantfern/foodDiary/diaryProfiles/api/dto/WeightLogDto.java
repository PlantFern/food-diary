package com.github.plantfern.foodDiary.diaryProfiles.api.dto;


public record WeightLogDto(
        Long id,
        Long diaryProfileId,
        Float weight
) { }
