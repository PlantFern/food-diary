package com.github.plantfern.foodDiary.specialists.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.specialists.api.RelationType;
import com.github.plantfern.foodDiary.specialists.api.apis.UserRelationApi;
import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.api.events.UserRelationActivated;
import com.github.plantfern.foodDiary.specialists.api.events.UserRelationInactivated;
import com.github.plantfern.foodDiary.specialists.domain.mappers.UserRelationMapper;
import com.github.plantfern.foodDiary.specialists.api.UserRelationStatus;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.specialists.domain.repositories.UserRelationRepository;
import com.github.plantfern.foodDiary.specialists.domain.repositories.UserRelationStatusRepository;
import com.github.plantfern.foodDiary.specialists.domain.security.UserRelationPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.UserApi;

import jakarta.persistence.EntityExistsException;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
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
            DiaryProfileApi diaryProfileApi
    ) {
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

        var specialist = specialistService.getById(specialistId);
        var diaryProfileOwnerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);

        userRelationPolicy.ensureCanCreate(
                currentUser,
                diaryProfileOwnerId,
                specialist.userId()
        );

        // Только одна активная связь типа SPECIALIST на профиль дневника
        if (relationType == RelationType.SPECIALIST
                && hasActiveSpecialistControlInternal(diaryProfileId)) {
            throw new IllegalStateException(
                    "Уже есть активная связь типа «Специалист». Завершите её, прежде чем подключать другого специалиста."
            );
        }

        var pendingStatus = userRelationStatusRepository.getByCode(UserRelationStatus.PENDING.name());

        var existingOpt = userRelationRepository.findByDiaryProfileIdAndSpecialistId(
                diaryProfileId, specialistId);

        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();
            UserRelationStatus status;
            try {
                status = existing.getStatusCode();
            } catch (Exception e) {
                status = null;
            }

            // После отмены или завершения клиент может снова отправить заявку
            if (status == UserRelationStatus.CANCELED || status == UserRelationStatus.ENDED) {
                existing.setRelationType(relationType);
                existing.setUserRelationStatusEntity(pendingStatus);
                userRelationRepository.save(existing);
                return;
            }

            if (status == UserRelationStatus.PENDING) {
                throw new IllegalStateException("Заявка уже отправлена и ожидает ответа специалиста");
            }
            if (status == UserRelationStatus.ACTIVE) {
                throw new IllegalStateException("Связь с этим специалистом уже активна");
            }

            throw new IllegalStateException("Relation between this profile and specialist already exists");
        }

        try {
            userRelationRepository.save(new UserRelationEntity(
                    diaryProfileId,
                    specialistId,
                    relationType,
                    pendingStatus
            ));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Relation between this profile and specialist already exists", ex);
        }
    }

    @Transactional
    public void activate(Long userRelationId) {
        var userRelation = userRelationRepository
                .findById(userRelationId)
                .orElseThrow(
                        () -> new EntityExistsException("user relation not found")
                );

        var specialist = specialistService
                .getById(userRelation.getSpecialistId());
        var diaryProfileOwnerId = diaryProfileApi.
                getOwnerUserIdInternal(userRelation.getDiaryProfileId());

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

        // UserRelationActivated(diaryProfileUserId, specialistUserId, isExtended)
        // isExtended = true only for SPECIALIST type (can set goals/settings);
        // OBSERVER still gets visibility via canSee (row with isExtended=false)
        boolean isExtended = userRelation.getRelationType() == RelationType.SPECIALIST;
        events.publishEvent(new UserRelationActivated(
                diaryProfileOwnerId,
                specialist.userId(),
                isExtended
        ));
    }

    @Transactional
    public void cancel(Long userRelationId) {
        var userRelation = userRelationRepository
                .findById(userRelationId)
                .orElseThrow(
                        () -> new EntityExistsException("user relation not found")
                );
        var specialistId = specialistService.getById(userRelation.getSpecialistId());

        userRelationPolicy.ensureCanActivateOrReject(
                currentUser,
                userRelation.getUserRelationStatusEntity().getCode(),
                specialistId.userId()
        );

        userRelation.setUserRelationStatusEntity(
                userRelationStatusRepository
                        .getByCode(UserRelationStatus.CANCELED.toString())
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
        var specialist = specialistService.getById(userRelation.getSpecialistId());

        userRelationPolicy.ensureCanEnd(
                currentUser,
                userRelation.getUserRelationStatusEntity().getCode(),
                specialist.userId(),
                userRelation.getDiaryProfileId());

        userRelation.setUserRelationStatusEntity(
                userRelationStatusRepository
                        .getByCode(UserRelationStatus.ENDED.toString())
        );

        userRelationRepository.save(userRelation);

        var diaryProfileOwnerId = diaryProfileApi.getOwnerUserIdInternal(userRelation.getDiaryProfileId());
        events.publishEvent(new UserRelationInactivated(
                userRelation.getId(),
                diaryProfileOwnerId,
                specialist.userId()
        ));
    }

    @Transactional(readOnly = true)
    public UserRelationEntity getById(Long userRelationId) {
        var userRelation = userRelationRepository
                .findById(userRelationId)
                .orElseThrow(
                        () -> new EntityExistsException("user relation not found")
                );

        var diaryProfileUserId = diaryProfileApi.getOwnerUserIdInternal(userRelation.getDiaryProfileId());

        userRelationPolicy.ensureCanGet(
                currentUser,
                userRelation,
                diaryProfileUserId
        );

        return userRelation;
    }

    @Transactional(readOnly = true)
    public List<UserRelationDto> getByDiaryProfileId(Long diaryProfileId) {

        var diaryProfileUserId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);

        userRelationPolicy.ensureClientAccess(currentUser, diaryProfileUserId);

        return getByDiaryProfileIdInternal(diaryProfileId);
    }

    @Transactional(readOnly = true)
    public List<UserRelationDto> getBySpecialistId(Long specialistId) {

        var specialistUserId = specialistService.getById(specialistId).userId();

        userRelationPolicy.ensureSpecialistAccess(currentUser, specialistUserId);

        return userRelationRepository
                .findAllBySpecialistId(specialistId)
                .stream().map(userRelationMapper::toDto)
                .toList();
    }


    //region Internal methods

    @Override
    @Transactional(readOnly = true)
    public List<UserRelationDto> getByDiaryProfileIdInternal(Long diaryProfileId) {
        return userRelationRepository
                .findAllByDiaryProfileId(diaryProfileId)
                .stream().map(userRelationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRelationDto> getBySpecialistIdInternal(Long specialistId) {
        return userRelationRepository
                .findAllBySpecialistId(specialistId)
                .stream().map(userRelationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDiaryProfileIdAndSpecialistIdInternal(
            Long diaryProfileId,
            Long specialistId
    ){
        return userRelationRepository
                .existsByDiaryProfileIdAndSpecialistId(diaryProfileId, specialistId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveSpecialistControlInternal(Long diaryProfileId) {
        if (diaryProfileId == null) return false;
        var activeStatus = userRelationStatusRepository.getByCode(UserRelationStatus.ACTIVE.name());
        return userRelationRepository.existsByDiaryProfileIdAndRelationTypeAndUserRelationStatusEntity(
                diaryProfileId,
                RelationType.SPECIALIST,
                activeStatus
        );
    }
    //endregion
}
