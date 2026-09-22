package com.github.plantfern.foodDiary.meals.domain.services;


import com.github.plantfern.foodDiary.meals.domain.entities.MealTypeEntity;
import com.github.plantfern.foodDiary.meals.domain.repositories.MealTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class MealTypeService {

    private final MealTypeRepository mealTypeRepository;

    @Transactional(readOnly = true)
    public List<MealTypeEntity> getAll() {
        return mealTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MealTypeEntity getById(Long id) {
        return mealTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal type not found"));
    }

    @Transactional(readOnly = true)
    public MealTypeEntity getByCode(String code) {
        return mealTypeRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Meal type not found"));
    }
}
