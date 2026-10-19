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

    /**
     * Blocks only the diary owner when an ACTIVE SPECIALIST relation controls goals.
     * The controlling specialist must still be able to create/update via ensureCanWrite.
     */
    private void ensureOwnerNotLockedBySpecialist(Long diaryProfileId, Long ownerUserId) {
        if (!currentUser.requireId().equals(ownerUserId)) {
            return;
        }
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
        ensureOwnerNotLockedBySpecialist(diaryProfileId, diaryProfile.userId());

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

        goalRepository.findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .ifPresent(last -> {
                    last.setDeletedDate();
                    goalRepository.save(last);
                });

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
        // Owner may only edit goals they authored; specialist with extended write may edit any.
        if (currentUser.requireId().equals(diaryProfileUserId)) {
            diaryProfilePolicy.ensureCreatedBy(currentUser, oldGoal.getCreatedById());
        }
        ensureOwnerNotLockedBySpecialist(oldGoal.getDiaryProfileId(), diaryProfileUserId);

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

        var goal = goalRepository.save(
                new GoalEntity(
                        oldGoal.getDiaryProfileId(),
                        plannedWeight,
                        startDate,
                        plannedEndDate,
                        currentUser.requireId()
                )
        );

        if (goalNutrientList.isEmpty())
            return goalMapper.toDto(goal);

        var newId = goal.getId();
        for (var goalNutrient : goalNutrientList) {
            goal.getGoalNutrientSet().add(
                    new GoalNutrientEntity(
                            newId,
                            goalNutrient.nutrientId(),
                            goalNutrient.amount()
                    )
            );
        }

        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public void delete(Long goalId) {
        var goal = goalRepository
                .findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with such id not found"));

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(goal.getDiaryProfileId());

        diaryProfilePolicy.ensureCanWrite(currentUser, diaryProfileUserId);
        if (currentUser.requireId().equals(diaryProfileUserId)) {
            diaryProfilePolicy.ensureCreatedBy(currentUser, goal.getCreatedById());
        }
        ensureOwnerNotLockedBySpecialist(goal.getDiaryProfileId(), diaryProfileUserId);

        goal.setDeletedDate();
        goalRepository.save(goal);
    }

    @Transactional(readOnly = true)
    public GoalDto getById(Long goalId) {
        var goal = goalRepository
                .findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with such id not found"));

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(goal.getDiaryProfileId());

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return goalMapper.toDto(goal);
    }

    @Transactional(readOnly = true)
    public GoalDto getActiveByDiaryProfileId(Long diaryProfileId) {
        var foundDiaryDiaryProfile = diaryProfileService.getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, foundDiaryDiaryProfile.getUserId());

        return goalRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .filter(g -> g.getDeletedAt() == null)
                .map(goalMapper::toDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> getAllByDiaryProfileId(Long diaryProfileId) {
        var diaryProfileUserId = diaryProfileService.getOwnerUserIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return goalRepository.findAllByDiaryProfileId(diaryProfileId).stream()
                .map(goalMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GoalDto getActiveByDiaryProfileInternal(Long diaryProfileId) {
        return goalRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .filter(g -> g.getDeletedAt() == null)
                .map(goalMapper::toDto)
                .orElse(null);
    }

    private LocalDate resolvePlannedEndDate(
            Long diaryProfileId,
            Float plannedWeight,
            LocalDate startDate
    ) {
        if (plannedWeight == null) {
            return startDate.plusWeeks(MAINTAIN_WEEKS);
        }

        Float currentWeight = weightLogRepository
                .findFirstByDiaryProfileIdOrderByLoggedAtDesc(diaryProfileId)
                .map(WeightLogEntity::getWeightKg)
                .orElse(null);

        if (currentWeight == null) {
            return startDate.plusWeeks(MAINTAIN_WEEKS);
        }

        float delta = plannedWeight - currentWeight;
        if (Math.abs(delta) <= MAINTAIN_THRESHOLD_KG) {
            return startDate.plusWeeks(MAINTAIN_WEEKS);
        }

        float perWeek = delta < 0 ? LOSE_KG_PER_WEEK : GAIN_KG_PER_WEEK;
        int weeks = (int) Math.ceil(Math.abs(delta) / perWeek);
        weeks = Math.max(MIN_WEEKS, Math.min(MAX_WEEKS, weeks));
        return startDate.plusWeeks(weeks);
    }
}
