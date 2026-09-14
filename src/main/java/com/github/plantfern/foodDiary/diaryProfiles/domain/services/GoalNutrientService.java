package com.github.plantfern.foodDiary.diaryProfiles.domain.services;

import com.github.plantfern.foodDiary.common.services.NutrientService;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.GoalNutrientApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.GoalNutrientMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.GoalNutrientRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@AllArgsConstructor
@Service
public class GoalNutrientService implements GoalNutrientApi {

    private final GoalNutrientRepository goalNutrientRepository;
    private final GoalNutrientMapper goalNutrientMapper;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;
    private final GoalService goalService;
    private final NutrientService nutrientService;


    @Transactional
    public void addToGoal(Long goalId, Long nutrientId, Float amount) {

        if(amount < 0)
            throw new IllegalArgumentException("Amount of nutrient cannot be less then 0");

        var nutrient = nutrientService.getById(nutrientId);

        var goal = goalService
                .getByIdInternal(goalId);

        diaryProfilePolicy.ensureCreatedBy(currentUser, goal.createdById());

        goalNutrientRepository.save(
                new GoalNutrientEntity(
                        goalId,
                        nutrient.getId(),
                        amount
                )
        );
    }

    @Transactional
    public void updateGoalNutrient(
            Long goalNutrientId,
            Long goalId,
            Long nutrientId,
            Float amount
    ) {

        var goalNutrient = goalNutrientRepository
                .findById(goalNutrientId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Goal nutrient with such id not found")
                );

        diaryProfilePolicy.ensureCreatedBy(
                currentUser,
                goalNutrient.getGoal().getCreatedById()
        );

        goalNutrient.setGoalId(goalId);
        goalNutrient.setNutrientId(nutrientId);
        goalNutrient.setAmount(amount);

        goalNutrientRepository.save(goalNutrient);
    }

    @Transactional
    public void removeFromGoal(Long goalNutrientId) {

        var goalNutrient = goalNutrientRepository
                .findById(goalNutrientId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Goal nutrient with such id not found")
                );

        var goal = goalService
                .getByIdInternal(goalNutrient.getGoalId());

        diaryProfilePolicy.ensureCreatedBy(currentUser, goal.createdById());

        goalNutrientRepository.delete(goalNutrient);
    }


    @Transactional(readOnly = true)
    @Override
    public List<GoalNutrientDto> getAllByGoalIdInternal(Long goalId) {
        return goalNutrientRepository
                .findByGoalId(goalId)
                .stream()
                .map(goalNutrientMapper::toDto)
                .toList();
    }
}
