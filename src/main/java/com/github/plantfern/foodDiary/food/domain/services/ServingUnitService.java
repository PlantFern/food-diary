package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.food.domain.entities.ServingUnitEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.ServingUnitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@AllArgsConstructor
@Service
public class ServingUnitService {

    private final ServingUnitRepository servingUnitRepository;


    @Transactional(readOnly = true)
    public boolean existsById(Long servingUnitId) {
        return servingUnitRepository.existsById(servingUnitId);
    }

    @Transactional(readOnly = true)
    public Long getIdByCode(String code) {
        return servingUnitRepository.findByCode(code)
                .map(ServingUnitEntity::getId)
                .orElseThrow(() -> new EntityNotFoundException("Serving unit not found: " + code));
    }
}
