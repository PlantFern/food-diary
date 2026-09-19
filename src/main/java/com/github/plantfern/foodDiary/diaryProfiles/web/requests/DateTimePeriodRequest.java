package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import java.time.LocalDateTime;

public record DateTimePeriodRequest(
        LocalDateTime startPeriod,
        LocalDateTime endPeriod
) {
}
