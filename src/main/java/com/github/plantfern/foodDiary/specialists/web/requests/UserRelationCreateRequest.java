package com.github.plantfern.foodDiary.specialists.web.requests;

import com.github.plantfern.foodDiary.specialists.api.RelationType;

public record UserRelationCreateRequest(
        Long diaryProfile,
        Long specialistId,
        String relationType
) { }
