package com.github.plantfern.foodDiary.common.domain.services;


import com.github.plantfern.foodDiary.common.domain.entities.NutrientEntity;
import com.github.plantfern.foodDiary.common.domain.repositories.NutrientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


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

    public Set<Long> getAllByIdNotIn(Set<Long> ids) {
        return nutrientRepository
                .findAllByIdNotIn(ids)
                .stream()
                .map(NutrientEntity::getId)
                .collect(Collectors.toSet());
    }
}
