package com.github.plantfern.foodDiary.diaryProfiles.api.apis;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;

import java.util.List;

public interface GoalApi {

    GoalDto getActiveByDiaryProfile(Long diaryDiaryProfile);

    GoalDto getById(Long id);
}
