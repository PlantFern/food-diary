package com.github.plantfern.foodDiary.food.domain.mappers;



import com.github.plantfern.foodDiary.food.api.dto.BrandedProductDto;
import com.github.plantfern.foodDiary.food.domain.entities.BrandedProductEntity;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface BrandedProductMapper {

    BrandedProductDto toDto(BrandedProductEntity foodServingEntity);

    BrandedProductEntity toEntity(BrandedProductDto foodServingDto);

    List<BrandedProductDto> toListDto(List<BrandedProductEntity> foodServingEntityList);
}
