package com.github.plantfern.foodDiary.specialists.api.events;

import com.github.plantfern.foodDiary.specialists.api.RelationType;

public record UserRelationActivated(
        Long diaryProfileUserId,
        Long specialistUserId
) { }
