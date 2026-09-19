package com.github.plantfern.foodDiary.specialists.api.dto;


public record SpecialistDto(
        Long id,
        Long userId,
        Boolean isActive
) {
}
