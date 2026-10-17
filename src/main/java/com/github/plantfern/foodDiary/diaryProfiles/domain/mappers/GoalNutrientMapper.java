package com.github.plantfern.foodDiary.diaryProfiles.domain.mappers;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GoalNutrientEntity;
import org.mapstruct.Mapper;

import java.util.List;


/**
 * Explicit mapping: goal_nutrients.id must NOT leak into nutrientId
 * (that produced "fiber 2300g" when row id was 5 and nutrient was ENERGY).
 */
@Mapper(componentModel = "spring")
public interface GoalNutrientMapper {

    default GoalNutrientDto toDto(GoalNutrientEntity e) {
        if (e == null) return null;
        return new GoalNutrientDto(
                e.getId(),
                e.getNutrientId(),
                e.getAmount()
        );
    }

    default GoalNutrientEntity toEntity(GoalNutrientDto dto) {
        if (dto == null) return null;
        return new GoalNutrientEntity(
                null,
                dto.nutrientId(),
                dto.amount()
        );
    }

    default List<GoalNutrientDto> toDtoList(List<GoalNutrientEntity> list) {
        if (list == null) return List.of();
        return list.stream().map(this::toDto).toList();
    }
}
