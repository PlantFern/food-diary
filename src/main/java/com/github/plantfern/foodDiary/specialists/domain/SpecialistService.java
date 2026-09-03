package com.github.plantfern.foodDiary.specialists.domain;


import com.github.plantfern.foodDiary.specialists.api.SpecialistApi;
import com.github.plantfern.foodDiary.specialists.api.SpecialistDto;
import com.github.plantfern.foodDiary.specialists.domain.repositories.SpecialistRepository;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.UserApi;
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


    public void updateActivity(Long targetId, @NotNull Boolean value){
        var specialist = specialistRepository.findByUserId(currentUser.requireId());

        specialist.setIsActive(value);
        var result = specialistRepository.save(specialist);
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
