package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DatePeriodRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startPeriod,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endPeriod
) {
}
