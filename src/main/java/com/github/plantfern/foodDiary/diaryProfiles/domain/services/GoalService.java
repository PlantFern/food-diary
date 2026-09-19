package com.github.plantfern.foodDiary.diaryProfiles.domain.services;

import com.github.plantfern.foodDiary.diaryProfiles.api.apis.GoalApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.GoalMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.GoalRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@Service
public class GoalService implements GoalApi {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final DiaryProfileService diaryProfileService;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;


    @Transactional
    public GoalDto create(
            Long diaryProfileId,
            Long plannedWeight,
            LocalDate startDate,
            LocalDate plannedEndDate,
            List<GoalNutrientDto> goalNutrientList
    ) {

        var diaryProfile = diaryProfileService.findByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanWrite(currentUser, diaryProfile.userId());

        goalRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .ifPresent(
                        goal -> {
                            goal.setActualEndedDay();
                            goalRepository.save(goal);
                        }
                );

        var goal = goalRepository.save(
                new GoalEntity(
                        diaryProfileId,
                        plannedWeight,
                        startDate,
                        plannedEndDate,
                        currentUser.requireId()
                )
        );

        var goalId = goal.getId();
        for( var goalNutrient : goalNutrientList ) {
            goal.getGoalNutrientList().add(
                    new GoalNutrientEntity(
                            goalId,
                            goalNutrient.nutrientId(),
                            goalNutrient.amount()
                    )
            );
        }

        return goalMapper.toDto(goal);
    }

    @Transactional
    public GoalDto update(
            Long goalId,
            Long plannedWeight,
            LocalDate startDate,
            LocalDate plannedEndDate,
            List<GoalNutrientDto> goalNutrientDtos
    ) {

        var oldGoal = goalRepository
                .findById(goalId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Goal with such id not found")
                );

        diaryProfilePolicy.ensureCreatedBy(currentUser, oldGoal.getCreatedById());

        oldGoal.setDeletedDate();
        goalRepository.save(oldGoal);

        var newGoal = goalRepository.save(
                new GoalEntity(
                        oldGoal.getDiaryProfileId(),
                        plannedWeight,
                        startDate,
                        plannedEndDate,
                        oldGoal.getCreatedById()
                )
        );

        var currentGoalId = newGoal.getId();
        for( var goalNutrient : goalNutrientDtos ) {
            newGoal.getGoalNutrientList().add(
                    new GoalNutrientEntity(
                            currentGoalId,
                            goalNutrient.nutrientId(),
                            goalNutrient.amount()
                    )
            );
        }

        return goalMapper.toDto(goalRepository.save(newGoal));
    }

    @Transactional
    public void delete(
            Long goalId
    ) {

        var goal = goalRepository
                .findById(goalId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Goal with such id not found")
                );

        diaryProfilePolicy.ensureCreatedBy(currentUser, goal.getCreatedById());

        goal.setDeletedDate();

        goalRepository.delete(goal);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getAllByDiaryProfile(Long diaryProfile) {

        var diaryProfileUserId = diaryProfileService.findById(diaryProfile).userId();

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return goalRepository
                .findAllByDiaryProfileId(diaryProfile)
                .stream()
                .map(goalMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalDto getLatestByDiaryProfile(Long diaryDiaryProfileId) {
        var goal = goalRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryDiaryProfileId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Any goal not found")
                );

        return goalMapper.toDto(goal);
    }

    @Transactional(readOnly = true)
    public GoalDto getActiveByDiaryProfile(Long diaryDiaryProfileId) {

        var goal = goalRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryDiaryProfileId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Any goal not found")
                );

        diaryProfilePolicy.ensureCanGet(currentUser, goal.getDiaryProfile().getUserId());

        if(goal.getActualEndDate() != null)
            return null;
        return goalMapper.toDto(goal);
    }

    @Transactional(readOnly = true)
    public GoalDto getById(Long goalId) {

        var foundGoal = goalRepository
                .findById(goalId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Goal with such id not found")
                );

        diaryProfilePolicy.ensureCanGet(currentUser, foundGoal.getDiaryProfile().getUserId() );

        return goalMapper.toDto(foundGoal);
    }


    @Transactional(readOnly = true)
    @Override
    public GoalDto getActiveByDiaryProfileInternal(Long diaryDiaryProfileId) {
        var goal = goalRepository
                    .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryDiaryProfileId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Any goal not found")
                );

        if (goal.getActualEndDate() == null)
            return goalMapper.toDto(goal);
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public GoalDto getByIdInternal(Long id) {
        return goalRepository
                .findById(id)
                .map(goalMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Goal with such id not founded")
                );
    }
}
