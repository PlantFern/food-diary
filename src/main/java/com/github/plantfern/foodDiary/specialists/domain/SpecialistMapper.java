package com.github.plantfern.foodDiary.specialists.domain;

import com.github.plantfern.foodDiary.specialists.SpecialistDto;
import com.github.plantfern.foodDiary.specialists.domain.entities.SpecialistEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialistMapper {
    SpecialistDto toDto(SpecialistEntity specialistEntity);

    SpecialistEntity toEntity(SpecialistDto specialistDto);

    List<SpecialistDto> toListDto(List<SpecialistEntity> specialistEntityList);
}
