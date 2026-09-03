package com.github.plantfern.foodDiary.specialists.api.events;

public record UserRelationInactivated(
        Long relationId,
        Long diaryProfileUserId,
        Long specialistUserId
) { }
