package com.github.plantfern.foodDiary.diaryProfiles.domain.services;

import com.github.plantfern.foodDiary.diaryProfiles.api.apis.GoalNutrientApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.GoalNutrientMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.GoalNutrientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@AllArgsConstructor
@Service
public class GoalNutrientService implements GoalNutrientApi {

    private final GoalNutrientRepository goalNutrientRepository;
    private final GoalNutrientMapper goalNutrientMapper;


    @Override
    public List<GoalNutrientDto> getAllByGoalId(Long goalId) {
        return goalNutrientRepository
                .findByGoalId(goalId)
                .stream()
                .map(goalNutrientMapper::toDto)
                .toList();
    }
}
