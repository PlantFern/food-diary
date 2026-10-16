package com.github.plantfern.foodDiary.common.domain.services;


import com.github.plantfern.foodDiary.common.domain.entities.NutrientEntity;
import com.github.plantfern.foodDiary.common.domain.repositories.NutrientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
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

    public List<NutrientEntity> findAllOrdered() {
        return nutrientRepository.findAll().stream()
                .sorted(Comparator.comparing(NutrientEntity::getId))
                .toList();
    }

    public boolean existsAllByIdIn(Set<Long> ids) {
        if(ids == null || ids.isEmpty()) {
            return true;
        }
        return nutrientRepository.countByIdIn(ids) == ids.size();
    }

    public Long getFirstByIdNotIn(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return nutrientRepository.findAll().stream()
                    .findFirst()
                    .map(NutrientEntity::getId)
                    .orElse(1L);
        }
        return nutrientRepository
                .findFirstByIdNotIn(ids)
                .map(NutrientEntity::getId)
                .orElseGet(() -> nutrientRepository.findAll().stream()
                        .findFirst()
                        .map(NutrientEntity::getId)
                        .orElse(1L));
    }

    public Set<Long> getAllByIdNotIn(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return nutrientRepository.findAll().stream()
                    .map(NutrientEntity::getId)
                    .collect(Collectors.toSet());
        }
        return nutrientRepository
                .findAllByIdNotIn(ids)
                .stream()
                .map(NutrientEntity::getId)
                .collect(Collectors.toSet());
    }
}
