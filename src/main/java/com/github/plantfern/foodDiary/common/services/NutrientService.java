package com.github.plantfern.foodDiary.common.services;


import com.github.plantfern.foodDiary.common.entities.NutrientEntity;
import com.github.plantfern.foodDiary.common.repositories.NutrientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;


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

    public boolean existsAllByIdIn(Set<Long> ids) {
        if(ids == null || ids.isEmpty()) {
            return true;
        }
        return nutrientRepository.countByIdIn(ids) == ids.size();
    }

    public Long getFirstByIdNotIn(Set<Long> ids) {
        return nutrientRepository
                .findFirstByIdNotIn(ids)
                .map(NutrientEntity::getId)
                .orElse(null);
    }
}
