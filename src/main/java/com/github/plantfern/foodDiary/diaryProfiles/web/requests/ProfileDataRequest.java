package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ProfileDataRequest(
        @Min(50) Float height,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
        Long genderId
) {
}
