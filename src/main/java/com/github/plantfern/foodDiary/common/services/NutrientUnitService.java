package com.github.plantfern.foodDiary.common.services;

import com.github.plantfern.foodDiary.common.entities.NutrientUnitEntity;
import com.github.plantfern.foodDiary.common.repositories.NutrientUnitRepository;
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
