package com.github.plantfern.foodDiary.diaryProfiles.domain.dto;

import java.time.LocalDate;

public record DiaryProfileDto(
        Long id,
        Long userId,
        Float height,
        LocalDate birthDate,
        String genderCode
) { }
