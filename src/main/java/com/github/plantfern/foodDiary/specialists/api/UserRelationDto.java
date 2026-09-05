package com.github.plantfern.foodDiary.specialists.api;

import com.github.plantfern.foodDiary.specialists.domain.UserRelationStatus;


public record UserRelationDto(
        Long diaryProfileId,
        Long specialistId,
        RelationType relationType,
        UserRelationStatus relationStatusCode) {
}
