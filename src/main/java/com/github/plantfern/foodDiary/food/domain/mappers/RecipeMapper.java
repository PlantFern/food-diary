package com.github.plantfern.foodDiary.food.domain.mappers;


import com.github.plantfern.foodDiary.food.api.dto.RecipeDto;
import com.github.plantfern.foodDiary.food.domain.entities.RecipeEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface RecipeMapper {

    RecipeDto toDto(RecipeEntity foodServingEntity);

    RecipeEntity toEntity(RecipeDto foodServingDto);

    List<RecipeDto> toListDto(List<RecipeEntity> foodServingEntityList);
}
