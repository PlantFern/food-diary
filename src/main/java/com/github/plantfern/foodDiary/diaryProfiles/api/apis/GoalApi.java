package com.github.plantfern.foodDiary.diaryProfiles.api.apis;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;

public interface GoalApi {

    GoalDto getActiveByDiaryProfileInternal(Long diaryDiaryProfile);

    GoalDto getByIdInternal(Long id);
}
