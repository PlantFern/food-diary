package com.github.plantfern.foodDiary.specialists.api.dto;


public record SpecialistSearchItemDto(
        Long id,
        Long userId,
        Boolean isActive,
        String email,
        String login
) {
}
