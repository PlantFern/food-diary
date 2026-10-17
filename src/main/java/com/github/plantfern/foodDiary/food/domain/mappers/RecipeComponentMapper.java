package com.github.plantfern.foodDiary.food.domain.mappers;


import com.github.plantfern.foodDiary.food.api.dto.RecipeComponentDto;
import com.github.plantfern.foodDiary.food.domain.entities.RecipeComponentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface RecipeComponentMapper {

    @Mapping(target = "componentId", source = "id")
    @Mapping(target = "productDescription", ignore = true)
    @Mapping(target = "servingUnitCode", ignore = true)
    @Mapping(target = "gramWeight", ignore = true)
    @Mapping(target = "nutrient", ignore = true)
    RecipeComponentDto toDto(RecipeComponentEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "recipeId", ignore = true)
    @Mapping(target = "productServing", ignore = true)
    RecipeComponentEntity toEntity(RecipeComponentDto dto);

    List<RecipeComponentDto> toListDto(List<RecipeComponentEntity> entities);
}
