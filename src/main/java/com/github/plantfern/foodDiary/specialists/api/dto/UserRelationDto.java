package com.github.plantfern.foodDiary.specialists.api.dto;


import com.github.plantfern.foodDiary.specialists.api.RelationType;
import com.github.plantfern.foodDiary.specialists.api.UserRelationStatus;

public record UserRelationDto(
        Long diaryProfileId,
        Long specialistId,
        RelationType relationType,
        UserRelationStatus relationStatusCode) {
}
