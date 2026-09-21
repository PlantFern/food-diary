package com.github.plantfern.foodDiary.food.domain.mappers;


import com.github.plantfern.foodDiary.food.api.dto.RecipeComponentDto;
import com.github.plantfern.foodDiary.food.domain.entities.RecipeComponentEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface RecipeComponentMapper {

    RecipeComponentDto toDto(RecipeComponentEntity foodServingEntity);

    RecipeComponentEntity toEntity(RecipeComponentDto foodServingDto);

    List<RecipeComponentDto> toListDto(List<RecipeComponentEntity> foodServingEntityList);
}
