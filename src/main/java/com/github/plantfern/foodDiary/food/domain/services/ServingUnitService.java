package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.food.domain.entities.ServingUnitEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.ServingUnitRepository;
import org.springframework.stereotype.Service;


@Service
public class ServingUnitService {

    private final ServingUnitRepository servingUnitRepository;

    public ServingUnitService(ServingUnitRepository servingUnitRepository) {
        this.servingUnitRepository = servingUnitRepository;
    }

    public boolean existsById(Long servingUnitId) {
        return servingUnitRepository.existsById(servingUnitId);
    }
}
