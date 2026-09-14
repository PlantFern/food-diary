package com.github.plantfern.foodDiary.diaryProfiles.api.apis;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;

import java.util.List;

public interface GoalNutrientApi {

    List<GoalNutrientDto> getAllByGoalId(Long goalId);
}
