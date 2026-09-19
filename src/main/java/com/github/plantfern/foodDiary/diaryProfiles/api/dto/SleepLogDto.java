package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import java.time.LocalDateTime;

public record SleepLogDto(
        Long id,
        Long diaryProfileId,
        LocalDateTime beganAt,
        LocalDateTime endedAt
) { }
