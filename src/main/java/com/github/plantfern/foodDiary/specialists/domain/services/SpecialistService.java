package com.github.plantfern.foodDiary.specialists.domain.services;


import com.github.plantfern.foodDiary.specialists.api.UserRelationStatus;
import com.github.plantfern.foodDiary.specialists.api.apis.SpecialistApi;
import com.github.plantfern.foodDiary.specialists.api.dto.SpecialistDto;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.specialists.domain.mappers.SpecialistMapper;
import com.github.plantfern.foodDiary.specialists.domain.entities.SpecialistEntity;
import com.github.plantfern.foodDiary.specialists.domain.repositories.SpecialistRepository;
import com.github.plantfern.foodDiary.specialists.domain.repositories.UserRelationRepository;
import com.github.plantfern.foodDiary.specialists.domain.security.SpecialistPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.api.UserApi;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Service
@Transactional
public class SpecialistService implements SpecialistApi {

    private final UserApi userApi;
    private final CurrentUser currentUser;

    private final SpecialistMapper specialistMapper;
    private final SpecialistRepository specialistRepository;
    private final SpecialistPolicy specialistPolicy;
    private final UserRelationRepository userRelationRepository;


    public SpecialistService(
            UserApi userApi,
            SpecialistRepository specialistRepository,
            SpecialistMapper specialistMapper,
            CurrentUser currentUser,
            SpecialistPolicy specialistPolicy,
            UserRelationRepository userRelationRepository){
        this.userApi = userApi;
        this.currentUser = currentUser;

        this.specialistMapper = specialistMapper;
        this.specialistRepository = specialistRepository;
        this.specialistPolicy = specialistPolicy;
        this.userRelationRepository = userRelationRepository;
    }


    @Transactional
    public void create(){
        var userId = currentUser.requireId();
        if (specialistRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Specialist already exists");
        }

        userApi.assignRolesInternal(userId, Set.of(RoleName.SPECIALIST));
        try {
            specialistRepository.save(new SpecialistEntity(userId));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Specialist already exists", ex);
        }
    }

    @Transactional
    public void updateActivity(){

        var specialist = specialistRepository.findByUserId(currentUser.requireId())
                .orElseThrow(() -> new EntityNotFoundException("Specialist doesn't exist"));

        specialist.setIsActive(!specialist.getIsActive());
        specialistRepository.save(specialist);
    }

    @Transactional
    public void approved(){

        specialistPolicy.ensureCanModerate(currentUser);

        var specialist = specialistRepository.findByUserId(currentUser.requireId())
                .orElseThrow(() -> new EntityNotFoundException("Specialist doesn't exist"));

        specialist.setIsApproved(!specialist.getIsApproved());
        specialistRepository.save(specialist);
    }

    @Transactional
    public void softDelete(){

        var userId = currentUser.requireId();

        var specialist = specialistRepository.findByUserId(currentUser.requireId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Specialist not found")
                );

        specialist.softDelete();
        specialist.setIsActive(false);

        var userRelations = userRelationRepository.findAllBySpecialistId(specialist.getId());

        for (UserRelationEntity userRelation : userRelations) {

            if(userRelation.getUserRelationStatusEntity().getCode().equals(UserRelationStatus.PENDING.name())){
                userRelation.getUserRelationStatusEntity().setCode(UserRelationStatus.CANCELED.name());
            }

            if(userRelation.getUserRelationStatusEntity().getCode().equals(UserRelationStatus.ACTIVE.name())){
                userRelation.getUserRelationStatusEntity().setCode(UserRelationStatus.ENDED.name());
            }
        }

        userApi.removeRoleInternal(userId, RoleName.SPECIALIST);

        specialistRepository.delete(specialist);
    }

    public void restore(){

        var userId = currentUser.requireId();

        if(specialistRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Specialist already exists");
        }

        var specialistFound = specialistRepository
                .findTopByUserIdAndDeletedAtIsNotNullOrderByDeletedAtDesc(currentUser.requireId())
                .orElseThrow(() -> new IllegalStateException("Deleted specialist doesn't exist"));

        specialistFound.restore();

        specialistFound.setIsActive(false);

        userApi.assignRolesInternal(userId, Set.of(RoleName.SPECIALIST));

        specialistRepository.save(specialistFound);
    }


    @Override
    @Transactional(readOnly = true)
    public SpecialistDto getById(Long targetId) {
        return specialistRepository
                .findById(targetId)
                .map(specialistMapper::toDto)
                .orElseThrow(
                        () -> new IllegalArgumentException("Specialist not found")
                );
    }

    @Transactional(readOnly = true)
    public SpecialistDto getByCurrentUser() {
        return specialistRepository.findByUserId(currentUser.requireId())
                .map(specialistMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Specialist for current user not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialistDto getByUserId(Long targetId) {
        return specialistRepository.findByUserId(targetId)
                .map(specialistMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Specialist not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialistDto> getAll() {
        return specialistMapper.toListDto(specialistRepository
                .findAll()
                .stream()
                .toList());
    }
}
