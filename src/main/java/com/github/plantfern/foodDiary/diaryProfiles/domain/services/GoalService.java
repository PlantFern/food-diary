package com.github.plantfern.foodDiary.diaryProfiles.domain.services;

import com.github.plantfern.foodDiary.common.domain.services.NutrientService;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.GoalApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.WeightLogEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.GoalMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.GoalRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.WeightLogRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.GoalNutrientRequest;
import com.github.plantfern.foodDiary.specialists.api.apis.UserRelationApi;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class GoalService implements GoalApi {

    private static final float LOSE_KG_PER_WEEK = 0.5f;
    private static final float GAIN_KG_PER_WEEK = 0.35f;
    private static final float MAINTAIN_THRESHOLD_KG = 0.3f;
    private static final int MIN_WEEKS = 2;
    private static final int MAX_WEEKS = 104;
    private static final int MAINTAIN_WEEKS = 12;

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final DiaryProfileService diaryProfileService;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;
    private final NutrientService nutrientService;
    private final WeightLogRepository weightLogRepository;
    private final UserRelationApi userRelationApi;

    private void ensureNotLockedBySpecialist(Long diaryProfileId) {
        if (userRelationApi.hasActiveSpecialistControlInternal(diaryProfileId)) {
            throw new IllegalStateException(
                    "Goals are managed by your specialist and cannot be changed"
            );
        }
    }

    @Transactional
    public GoalDto create(
            Long diaryProfileId,
            Float plannedWeight,
            LocalDate startDate,
            LocalDate plannedEndDate,
            List<GoalNutrientRequest> goalNutrientList
    ) {

        var diaryProfile = diaryProfileService.getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanWrite(currentUser, diaryProfile.userId());
        ensureNotLockedBySpecialist(diaryProfileId);

        if (goalNutrientList == null) {
            goalNutrientList = List.of();
        }

        boolean hasWeight = plannedWeight != null;
        boolean hasNutrients = !goalNutrientList.isEmpty();
        if (!hasWeight && !hasNutrients) {
            throw new IllegalArgumentException(
                    "At least planned weight or one nutrient goal is required"
            );
        }

        if (startDate == null) {
            startDate = LocalDate.now();
        }

        plannedEndDate = resolvePlannedEndDate(diaryProfileId, plannedWeight, startDate);

        var nutrientIds = goalNutrientList
                .stream()
                .map(GoalNutrientRequest::nutrientId)
                .collect(Collectors.toSet());
        if (!nutrientService.existsAllByIdIn(nutrientIds))
            throw new EntityNotFoundException("Not all nutrients found");

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

        if (goalNutrientList.isEmpty())
            return goalMapper.toDto(goal);

        var goalId = goal.getId();
        for (var goalNutrient : goalNutrientList) {
            goal.getGoalNutrientSet().add(
                    new GoalNutrientEntity(
                            goalId,
                            goalNutrient.nutrientId(),
                            goalNutrient.amount()
                    )
            );
        }

        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public GoalDto update(
            Long goalId,
            Float plannedWeight,
            LocalDate startDate,
            LocalDate plannedEndDate,
            List<GoalNutrientRequest> goalNutrientList
    ) {

        var oldGoal = goalRepository
                .findById(goalId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Goal with such id not found")
                );

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(oldGoal.getDiaryProfileId());

        diaryProfilePolicy.ensureCanWrite(currentUser, diaryProfileUserId);
        diaryProfilePolicy.ensureCreatedBy(currentUser, oldGoal.getCreatedById());
        ensureNotLockedBySpecialist(oldGoal.getDiaryProfileId());

        if (goalNutrientList == null) {
            goalNutrientList = List.of();
        }

        boolean hasWeight = plannedWeight != null;
        boolean hasNutrients = !goalNutrientList.isEmpty();
        if (!hasWeight && !hasNutrients) {
            throw new IllegalArgumentException(
                    "At least planned weight or one nutrient goal is required"
            );
        }

        if (startDate == null) {
            startDate = LocalDate.now();
        }

        plannedEndDate = resolvePlannedEndDate(
                oldGoal.getDiaryProfileId(),
                plannedWeight,
                startDate
        );

        var nutrientIds = goalNutrientList
                .stream()
                .map(GoalNutrientRequest::nutrientId)
                .collect(Collectors.toSet());
        if (!nutrientService.existsAllByIdIn(nutrientIds))
            throw new EntityNotFoundException("Not all nutrients found");

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

        if (goalNutrientList.isEmpty())
            return goalMapper.toDto(newGoal);

        var currentGoalId = newGoal.getId();
        for (var goalNutrient : goalNutrientList) {
            newGoal.getGoalNutrientSet().add(
                    new GoalNutrientEntity(
                            currentGoalId,
                            goalNutrient.nutrientId(),
                            goalNutrient.amount()
                    )
            );
        }

        return goalMapper.toDto(goalRepository.save(newGoal));
    }

    private LocalDate resolvePlannedEndDate(
            Long diaryProfileId,
            Float plannedWeight,
            LocalDate startDate
    ) {
        if (plannedWeight == null) {
            return startDate.plusWeeks(MAINTAIN_WEEKS);
        }

        float currentWeight = weightLogRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .map(WeightLogEntity::getWeight)
                .orElse(plannedWeight);

        float delta = plannedWeight - currentWeight;
        float absDelta = Math.abs(delta);

        if (absDelta < MAINTAIN_THRESHOLD_KG) {
            return startDate.plusWeeks(MAINTAIN_WEEKS);
        }

        float weeklyRate = delta < 0 ? LOSE_KG_PER_WEEK : GAIN_KG_PER_WEEK;
        int weeks = (int) Math.ceil(absDelta / weeklyRate);
        weeks = Math.max(MIN_WEEKS, Math.min(MAX_WEEKS, weeks));
        return startDate.plusWeeks(weeks);
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

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(goal.getDiaryProfileId());

        diaryProfilePolicy.ensureCanWrite(currentUser, diaryProfileUserId);
        diaryProfilePolicy.ensureCreatedBy(currentUser, goal.getCreatedById());
        ensureNotLockedBySpecialist(goal.getDiaryProfileId());

        goal.setDeletedDate();

        goalRepository.save(goal);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getAllByDiaryProfile(Long diaryProfile) {

        var diaryProfileUserId = diaryProfileService.getById(diaryProfile).getUserId();

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return goalRepository
                .findAllByDiaryProfileId(diaryProfile)
                .stream()
                .map(goalMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalDto getLatestByDiaryProfile(Long diaryDiaryProfileId) {

        var foundDiaryDiaryProfile = diaryProfileService.getById(diaryDiaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, foundDiaryDiaryProfile.getUserId());

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

        var diaryProfileUserId = diaryProfileService.getOwnerUserIdInternal(goal.getDiaryProfileId());
        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        if(goal.getActualEndDate() != null)
            throw new EntityNotFoundException("Any goal not found");
        return goalMapper.toDto(goal);
    }

    @Transactional(readOnly = true)
    public GoalDto getById(Long goalId) {

        var foundGoal = goalRepository
                .findById(goalId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Goal with such id not found")
                );

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(foundGoal.getDiaryProfileId());

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

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
