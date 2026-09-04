package com.github.plantfern.foodDiary.specialists.domain;


import com.github.plantfern.foodDiary.specialists.api.SpecialistApi;
import com.github.plantfern.foodDiary.specialists.api.SpecialistDto;
import com.github.plantfern.foodDiary.specialists.domain.entities.SpecialistEntity;
import com.github.plantfern.foodDiary.specialists.domain.repositories.SpecialistRepository;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.UserApi;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class SpecialistService implements SpecialistApi {

    private final UserApi userApi;
    private final CurrentUser currentUser;

    private final SpecialistMapper specialistMapper;
    private final SpecialistRepository specialistRepository;

    public SpecialistService(
            UserApi userApi,
            SpecialistRepository specialistRepository,
            SpecialistMapper specialistMapper,
            CurrentUser currentUser
    ){
        this.userApi = userApi;
        this.currentUser = currentUser;

        this.specialistMapper = specialistMapper;
        this.specialistRepository = specialistRepository;
    }

    public void create(){
        var userId = currentUser.requireId();
        if(specialistRepository.findByUserId(userId) != null)
            throw new IllegalStateException("Specialist already exists");

        specialistRepository.save(new SpecialistEntity(userId));
    }

    public void updateActivity(){
        var specialist = specialistRepository.findByUserId(currentUser.requireId());
        if(specialist == null)
            throw new EntityNotFoundException("Specialist doesn't exist");

        specialist.setIsActive(!specialist.getIsActive());
        specialistRepository.save(specialist);
    }


    @Override
    @Transactional(readOnly = true)
    public SpecialistDto findById(Long targetId) {
        return specialistRepository
                .findById(targetId)
                .map(specialistMapper::toDto)
                .orElseThrow(
                        () -> new IllegalArgumentException("Specialist not found")
                );
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialistDto findByUserId(Long targetId) {
        return specialistMapper.toDto(specialistRepository
                .findByUserId(targetId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialistDto> findAll() {
        return specialistMapper.toListDto(specialistRepository
                .findAll()
                .stream()
                .toList());
    }
}
