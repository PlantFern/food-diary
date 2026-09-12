package com.github.plantfern.foodDiary.specialists.domain;


import com.github.plantfern.foodDiary.diaryProfiles.api.DiaryProfileApi;
import com.github.plantfern.foodDiary.specialists.api.RelationType;
import com.github.plantfern.foodDiary.specialists.api.UserRelationApi;
import com.github.plantfern.foodDiary.specialists.api.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.api.events.UserRelationActivated;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.specialists.domain.repositories.UserRelationRepository;
import com.github.plantfern.foodDiary.specialists.domain.repositories.UserRelationStatusRepository;
import com.github.plantfern.foodDiary.specialists.domain.security.UserRelationPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.UserApi;

import jakarta.persistence.EntityExistsException;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class UserRelationService implements UserRelationApi {

    private final CurrentUser currentUser;
    private final UserApi userApi;
    private final UserRelationPolicy userRelationPolicy;

    private final UserRelationMapper userRelationMapper;
    private final UserRelationRepository userRelationRepository;
    private final SpecialistService specialistService;
    private final UserRelationStatusRepository userRelationStatusRepository;

    private final ApplicationEventPublisher events;
    @Lazy
    private final DiaryProfileApi diaryProfileApi;


    public UserRelationService(
            CurrentUser currentUser,
            UserApi userApi,
            UserRelationPolicy userRelationPolicy,

            UserRelationMapper userRelationMapper,
            UserRelationRepository userRelationRepository,
            UserRelationStatusRepository userRelationStatusRepository,
            SpecialistService specialistService,

            ApplicationEventPublisher events,
            DiaryProfileApi diaryProfileApi) {
        this.currentUser = currentUser;
        this.userApi = userApi;
        this.userRelationPolicy = userRelationPolicy;

        this.userRelationMapper = userRelationMapper;
        this.userRelationRepository = userRelationRepository;
        this.userRelationStatusRepository = userRelationStatusRepository;
        this.specialistService = specialistService;

        this.events = events;
        this.diaryProfileApi = diaryProfileApi;
    }


    @Transactional
    public void create(Long diaryProfileId, Long specialistId, RelationType relationType) {

        var specialist = specialistService.findById(specialistId);
        var diaryProfileOwnerId = diaryProfileApi.getOwnerUserId(diaryProfileId);

        userRelationPolicy.ensureCanCreate(
                currentUser,
                diaryProfileOwnerId,
                specialist.userId()
        );

        userRelationRepository.save(new UserRelationEntity(
                diaryProfileId,
                specialistId,
                relationType
        ));
    }

    @Transactional
    public void activate(Long userRelationId) {
        var userRelation = userRelationRepository
                .findById(userRelationId)
                .orElseThrow(
                        () -> new EntityExistsException("user relation not found")
                );

        var specialist = specialistService
                .findById(userRelation.getSpecialistId());
        var diaryProfileOwnerId = diaryProfileApi.
                getOwnerUserId(userRelation.getDiaryProfileId());

        userRelationPolicy.ensureCanActivateOrReject(
                currentUser,
                userRelation.getUserRelationStatusEntity().getCode(),
                specialist.userId()
        );

        userRelation.setUserRelationStatusEntity(
                userRelationStatusRepository
                        .getByCode(UserRelationStatus.ACTIVE.toString())
        );

        userRelationRepository.save(userRelation);

        events.publishEvent(new UserRelationActivated(
                specialist.userId(),
                diaryProfileOwnerId
        ));
    }

    @Transactional
    public void cancel(Long userRelationId) {
        var userRelation = userRelationRepository
                .findById(userRelationId)
                .orElseThrow(
                        () -> new EntityExistsException("user relation not found")
                );
        var specialistId = specialistService.findById(userRelation.getSpecialistId());

        userRelationPolicy.ensureCanActivateOrReject(
                currentUser,
                userRelation.getUserRelationStatusEntity().getCode(),
                specialistId.userId()
        );

        userRelation.setUserRelationStatusEntity(
                userRelationStatusRepository
                        .getByCode(UserRelationStatus.REJECTED.toString())
        );

        userRelationRepository.save(userRelation);
    }

    @Transactional
    public void deactivate(Long userRelationId) {
        var userRelation = userRelationRepository
                .findById(userRelationId)
                .orElseThrow(
                        () -> new EntityExistsException("user relation not found")
                );
        var specialistId = specialistService.findById(userRelation.getSpecialistId());

        userRelationPolicy.ensureCanEnd(
                currentUser,
                userRelation.getUserRelationStatusEntity().getCode(),
                specialistId.userId());

        userRelation.setUserRelationStatusEntity(
                userRelationStatusRepository
                        .getByCode(UserRelationStatus.REJECTED.toString())
        );

        userRelationRepository.save(userRelation);
    }

    @Transactional
    public UserRelationEntity findById(Long userRelationId) {
        var userRelation = userRelationRepository
                .findById(userRelationId)
                .orElseThrow(
                        () -> new IllegalArgumentException("UserRelation not found")
                );

        var diaryProfileUserId = diaryProfileApi.getOwnerUserId(userRelation.getDiaryProfileId());

        userRelationPolicy.ensureCanGet(
                currentUser,
                userRelation,
                diaryProfileUserId
        );

        return userRelation;
    }

    @Override
    public List<UserRelationDto> findByDiaryProfileId(Long diaryProfileId) {
        return userRelationRepository
                .findAllByDiaryProfileId(diaryProfileId)
                .stream().map(userRelationMapper::toDto)
                .toList();
    }

    @Override
    public List<UserRelationDto> findBySpecialistId(Long specialistId) {
        return userRelationRepository
                .findAllByDiaryProfileId(specialistId)
                .stream().map(userRelationMapper::toDto)
                .toList();
    }

    @Override
    public List<UserRelationDto> findAll() {
        return userRelationRepository
                .findAll()
                .stream()
                .map(userRelationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public boolean existsByDiaryProfileIdAndSpecialistId(
            Long diaryProfileId,
            Long specialistId
    ){
        return userRelationRepository
                .existsByDiaryProfileIdAndSpecialistId(diaryProfileId, specialistId);
    }
}