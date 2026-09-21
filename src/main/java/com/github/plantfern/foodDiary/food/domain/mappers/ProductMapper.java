package com.github.plantfern.foodDiary.food.domain.mappers;


import com.github.plantfern.foodDiary.food.api.dto.ProductDto;
import com.github.plantfern.foodDiary.food.domain.entities.ProductEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(ProductEntity foodServingEntity);

    ProductEntity toEntity(ProductDto foodServingDto);

    List<ProductDto> toListDto(List<ProductEntity> foodServingEntityList);
}
