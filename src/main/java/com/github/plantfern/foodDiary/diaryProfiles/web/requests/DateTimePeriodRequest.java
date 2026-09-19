package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record DateTimePeriodRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startPeriod,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endPeriod
) {
}
