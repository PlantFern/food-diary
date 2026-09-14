package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;

import java.time.LocalDateTime;

public record SleepLogDto(
        Long diaryProfile,
        LocalDateTime beganAt,
        LocalDateTime endedAt
) { }
