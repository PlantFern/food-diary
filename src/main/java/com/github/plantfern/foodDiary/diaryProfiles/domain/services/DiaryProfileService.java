package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.common.services.NutrientService;
import com.github.plantfern.foodDiary.diaryProfiles.api.ActivityLevel;
import com.github.plantfern.foodDiary.diaryProfiles.api.Gender;
import com.github.plantfern.foodDiary.diaryProfiles.api.GoalType;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.events.DiaryProfileCreated;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.WeightLogEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.DiaryProfileMapper;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryProfileDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.DiaryProfileRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.GenderRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.GoalRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.WeightLogRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;

import com.github.plantfern.foodDiary.users.api.CurrentUser;

import jakarta.persistence.EntityNotFoundException;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.max;


@Service
@Transactional
public class DiaryProfileService implements DiaryProfileApi {

    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;

    private final DiaryProfileRepository diaryProfileRepository;
    private final GenderRepository genderRepository;
    private final DiaryProfileMapper mapper;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final GoalRepository goalRepository;
    private final WeightLogRepository weightLogRepository;
    private final NutrientService nutrientService;

    @Autowired
    public DiaryProfileService(
            DiaryProfileRepository diaryProfileRepository,
            GenderRepository genderRepository,
            DiaryProfileMapper mapper,

            CurrentUser currentUser,
            DiaryProfilePolicy diaryProfilePolicy,

            ApplicationEventPublisher applicationEventPublisher,
            GoalRepository goalRepository,
            WeightLogRepository weightLogRepository,
            NutrientService nutrientService){
        this.diaryProfileRepository = diaryProfileRepository;
        this.genderRepository = genderRepository;
        this.mapper = mapper;

        this.currentUser = currentUser;
        this.diaryProfilePolicy = diaryProfilePolicy;
        this.applicationEventPublisher = applicationEventPublisher;
        this.goalRepository = goalRepository;
        this.weightLogRepository = weightLogRepository;
        this.nutrientService = nutrientService;
    }


    @Transactional
    public DiaryProfileDto create(Float height, LocalDate birthDate, Long genderId){
        var actorUser = currentUser.requireId();

        if (diaryProfileRepository.existsByUserId(actorUser)){
            throw new IllegalStateException("Diary profile for user already exists");
        }

        applicationEventPublisher.publishEvent(
                new DiaryProfileCreated(
                        actorUser
                )
                );

        var gender = genderRepository.findById(genderId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Gender with id: " + genderId + "doesn't exist"
                )
        );

        return mapper.toDto(
                diaryProfileRepository.save(
                        new DiaryProfileEntity(
                        actorUser,
                        height,
                        birthDate,
                        gender
                        )
                )
        );
    }

    @Transactional
    public DiaryProfileDto createWithCalculatedGoal(
            @NotNull Float height,
            @NotNull LocalDate birthDate,
            @NotNull Long genderId,
            @NotNull Float weight,
            @NotNull Float plannedWeight,
            @NotNull ActivityLevel activityLevel,
            @NotNull GoalType goalType
    ){

        var diaryProfileDto = create(height, birthDate, genderId);

        var gender = genderRepository
                .findById(genderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Gender not found")
                );

        var currentAge = LocalDate.now().minusYears(birthDate.getYear()).getYear();
        float bmr;
        if(gender.getCode().equals(Gender.FEMALE.name()))
            bmr = 10F * weight + 6.25F * height - 5F * currentAge - 161F;
        else
            bmr = 10F * weight + 6.25F * height - 5F * currentAge + 5F;

        float tdee = bmr * activityLevel.getCoefficient();

        float targetCalories = tdee * (1 + goalType.getCalorieAdjustment());

        float proteinAmount = goalType.getProteinRatio() * weight;
        float proteinKcal = proteinAmount * 4;

        float fatAmount = goalType.getFatRation() * weight;
        float fatKcal = fatAmount * 9;

        float carbAmount = max(targetCalories - proteinKcal - fatKcal, 0) / 4;

        var goal = goalRepository.save(
                new GoalEntity(
                        diaryProfileDto.id(),
                        plannedWeight,
                        LocalDate.now(),
                        null,
                        currentUser.requireId()
                )
        );
        goal.setGoalNutrientSet(
                Set.of(
                        new GoalNutrientEntity(goal.getId(), 1L, targetCalories),
                        new GoalNutrientEntity(goal.getId(), 2L, proteinAmount),
                        new GoalNutrientEntity(goal.getId(), 3L, fatAmount),
                        new GoalNutrientEntity(goal.getId(), 4L, carbAmount)
                )
        );
        goalRepository.saveAndFlush(goal);

        weightLogRepository.save(
                new WeightLogEntity(
                        diaryProfileDto.id(),
                        weight
                )
        );

        return diaryProfileDto;
    }

    @Transactional
    public DiaryProfileDto createWithGoal(
            @Null Float height,
            @Null LocalDate birthDate,
            @Null Long genderId,
            @Null Float weight,
            @Null Float plannedWeight,
            @Null LocalDate plannedEndDate,
            Map<Long, Float> goalNutrientMap
    ) {

        var diaryProfileDto = create(height, birthDate, genderId);

        if(weight != null)
            weightLogRepository.save(
                    new WeightLogEntity(
                            diaryProfileDto.id(),
                            weight
                    )
            );

        var goal = goalRepository.save(
                new GoalEntity(
                        diaryProfileDto.id(),
                        plannedWeight,
                        LocalDate.now(),
                        plannedEndDate,
                        currentUser.requireId()
                )
        );

        var setOfNutrients = goalNutrientMap
                .keySet();

        if (!nutrientService.existsAllByIdIn(setOfNutrients))
            throw new EntityNotFoundException("Nutrients not found");

        goalNutrientMap.forEach( (goalNutrientKey, goalNutrientValue) -> {

                    if (goalNutrientValue < 0)
                        throw new IllegalArgumentException("Amount of nutrient must me greater then or equal 0");

                    goal.getGoalNutrientSet().add(
                            new GoalNutrientEntity(
                                    goal.getId(),
                                    goalNutrientKey,
                                    goalNutrientValue
                            )
                    );
                }
        );

        goalRepository.save(goal);

        return diaryProfileDto;
    }

    @Transactional
    public DiaryProfileDto update(
            Long id,
            Float height,
            LocalDate birthDate,
            Long genderId
    ){
        var profile = diaryProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Diary profile with id: " + id + " doesn't exist"
                        ));

        diaryProfilePolicy.ensureIsOwner(
                currentUser,
                profile.getUserId()
        );

        profile.setHeight(height);
        profile.setBirthDate(birthDate);
        profile.setGender(
                this.genderRepository
                    .findById(genderId)
                    .orElseThrow(() -> new EntityNotFoundException(
                                    "Gender with id: " + genderId + "doesn't exist"
                            )));

        return mapper.toDto(diaryProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    List<Long> getOwnerUserIdList(List<Long> diaryProfileIds){

        return diaryProfileRepository.findAllUserIdsByIdIn(diaryProfileIds)
                .stream()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DiaryProfileEntity> getAllById(Collection<Long> ids) {

        var diaryProfiles = diaryProfileRepository
                .findAllById(ids)
                .stream()
                .toList();

        diaryProfilePolicy.ensureCanGetAll(
                currentUser,
                diaryProfiles
                        .stream()
                        .map(DiaryProfileEntity::getUserId)
                        .toList());

        return diaryProfiles;
    }

    @Transactional(readOnly = true)
    public DiaryProfileDto getByIdWithPolicy(Long diaryProfileId){

        var foundDiaryProfile = getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, foundDiaryProfile.userId());

        return foundDiaryProfile;
    }

    @Transactional(readOnly = true)
    DiaryProfileEntity getById(Long diaryProfileId) {

        return diaryProfileRepository
                .findById(diaryProfileId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Diary profile not found")
                );
    }


    //region Internal methods

    @Override
    @Transactional(readOnly = true)
    public DiaryProfileDto getByIdInternal(Long diaryProfileId) {

        var targetDiaryProfile = diaryProfileRepository
                .findById(diaryProfileId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Diary profile not found")
                );

        return mapper.toDto(targetDiaryProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdInternal(Long id) {
        return diaryProfileRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOwnerUserIdInternal(Long diaryProfileId){

        return diaryProfileRepository
                .findUserIdById(diaryProfileId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Dairy profile with such id not found")
                );
    }
    //endregion
}
