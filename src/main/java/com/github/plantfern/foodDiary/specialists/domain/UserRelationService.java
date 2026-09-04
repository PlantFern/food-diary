package com.github.plantfern.foodDiary.specialists.domain;


import com.github.plantfern.foodDiary.diaryProfiles.api.DiaryProfileApi;
import com.github.plantfern.foodDiary.specialists.api.RelationType;
import com.github.plantfern.foodDiary.specialists.api.UserRelationApi;
import com.github.plantfern.foodDiary.specialists.api.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.api.events.UserRelationActivated;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.specialists.domain.repositories.UserRelationRepository;
import com.github.plantfern.foodDiary.specialists.domain.repositories.UserRelationStatusRepository;
import com.github.plantfern.foodDiary.specialists.domain.security.RelationTypePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.UserApi;

import jakarta.persistence.EntityExistsException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class UserRelationService implements UserRelationApi {

    private final CurrentUser currentUser;
    private final UserApi userApi;
    private final RelationTypePolicy relationTypePolicy;

    private final UserRelationMapper userRelationMapper;
    private final UserRelationRepository userRelationRepository;
    private final SpecialistService specialistService;
    private final UserRelationStatusRepository userRelationStatusRepository;

    private final ApplicationEventPublisher events;
    private final DiaryProfileApi diaryProfileApi;

    public UserRelationService(
            CurrentUser currentUser,
            UserApi userApi,
            RelationTypePolicy relationTypePolicy,

            UserRelationMapper userRelationMapper,
            UserRelationRepository userRelationRepository,
            UserRelationStatusRepository userRelationStatusRepository,
            SpecialistService specialistService,

            ApplicationEventPublisher events,
            DiaryProfileApi diaryProfileApi) {
        this.currentUser = currentUser;
        this.userApi = userApi;
        this.relationTypePolicy = relationTypePolicy;

        this.userRelationMapper = userRelationMapper;
        this.userRelationRepository = userRelationRepository;
        this.userRelationStatusRepository = userRelationStatusRepository;
        this.specialistService = specialistService;

        this.events = events;
        this.diaryProfileApi = diaryProfileApi;
    }


    @Transactional
    public void create(Long diaryProfileUser, Long specialistId, RelationType relationType) {

        events.publishEvent(new UserRelationActivated(
                diaryProfileUser,
                diaryProfileUser,
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
        var specialistId = specialistService.findById(userRelation.getSpecialistId());

        relationTypePolicy.ensureCanActivateOrReject(
                currentUser,
                userRelation.getUserRelationStatusEntity().getCode(),
                specialistId.userId()
        );

        userRelation.setUserRelationStatusEntity(
                userRelationStatusRepository
                        .getByCode(UserRelationStatus.ACTIVE.toString())
        );

        userRelationRepository.save(userRelation);
    }

    @Transactional
    public void cancel(Long userRelationId) {
        var userRelation = userRelationRepository
                .findById(userRelationId)
                .orElseThrow(
                        () -> new EntityExistsException("user relation not found")
                );
        var specialistId = specialistService.findById(userRelation.getSpecialistId());

        relationTypePolicy.ensureCanActivateOrReject(
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

        relationTypePolicy.ensureCanEnd(
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

        var diaryProfileUserId = diaryProfileApi.findById(userRelation.getDiaryProfileId());

        relationTypePolicy.ensureCanGet(
                currentUser,
                userRelation,
                diaryProfileUserId.userId()
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