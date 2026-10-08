package com.github.plantfern.foodDiary.diaryProfiles.api.events;

import java.time.LocalDateTime;

public record DiaryProfileDeletedEvent(
        Long diaryProfileId,
        Long userId,
        LocalDateTime deletedAt
) {
}
