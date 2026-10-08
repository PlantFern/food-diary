package com.github.plantfern.foodDiary.specialists.api.events;


import java.time.LocalDateTime;


public record SpecialistDeletedEvent(
        Long userId,
        LocalDateTime deletedAt
) {
}
