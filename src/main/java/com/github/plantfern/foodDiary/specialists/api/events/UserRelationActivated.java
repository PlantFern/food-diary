package com.github.plantfern.foodDiary.specialists.api.events;

import com.github.plantfern.foodDiary.specialists.api.RelationType;

public record UserRelationActivated(
        Long relationId,
        Long diaryProfileUserId,
        Long specialistUserId,
        RelationType relationType
) { }
