package com.github.plantfern.foodDiary.diaryProfiles.api.dto;

import java.time.LocalDate;

public record DiaryProfileDto(
        Long id,
        Long userId,
        Float height,
        LocalDate birthDate,
        String genderCode
) { }
