package com.github.plantfern.foodDiary.food.domain.mappers;


import com.github.plantfern.foodDiary.food.api.dto.FoodServingDto;
import com.github.plantfern.foodDiary.food.domain.entities.FoodServingEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface FoodServingMapper {

    FoodServingDto toDto(FoodServingEntity foodServingEntity);

    FoodServingEntity toEntity(FoodServingDto foodServingDto);

    List<FoodServingDto> toListDto(List<FoodServingEntity> foodServingEntityList);
}
