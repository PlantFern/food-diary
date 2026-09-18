package com.github.plantfern.foodDiary.specialists.api;


public record UserRelationDto(
        Long diaryProfileId,
        Long specialistId,
        RelationType relationType,
        UserRelationStatus relationStatusCode) {
}
