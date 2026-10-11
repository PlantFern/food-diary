package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.food.domain.entities.ServingUnitEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.ServingUnitRepository;
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
}
