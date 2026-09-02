package com.github.plantfern.foodDiary.specialists;

import com.github.plantfern.foodDiary.specialists.domain.UserRelationStatus;


public record UserRelationDto(Long diaryProfileId, Long specialistId, UserRelationStatus relationStatusCode) {
}
