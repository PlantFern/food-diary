package com.github.plantfern.foodDiary.meals.domain.repositories;


import com.github.plantfern.foodDiary.meals.domain.entities.MealTemplateFoodRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MealTemplateFoodRecordRepository extends JpaRepository<MealTemplateFoodRecordEntity, Long> {

    List<MealTemplateFoodRecordEntity> findAllByMealTemplateId(Long templateId);
}
