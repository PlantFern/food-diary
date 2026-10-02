package com.github.plantfern.foodDiary.common.domain.services;

import com.github.plantfern.foodDiary.common.domain.entities.NutrientUnitEntity;
import com.github.plantfern.foodDiary.common.domain.repositories.NutrientUnitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class NutrientUnitService {


    private NutrientUnitRepository nutrientUnitRepository;

    public NutrientUnitEntity getById(Long id) {
        return nutrientUnitRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("No nutrientUnitEntity with such id")
                );
    }
}
