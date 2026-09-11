package com.github.plantfern.foodDiary.common.services;


import com.github.plantfern.foodDiary.common.entities.NutrientEntity;
import com.github.plantfern.foodDiary.common.repositories.NutrientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class NutrientService {

    private final NutrientRepository nutrientRepository;

    public NutrientEntity getById(Long id) {
        return nutrientRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("No nutrientEntity with such id")
                );
    }
}
