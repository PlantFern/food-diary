package com.github.plantfern.foodDiary.specialists;


import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface SpecialistApi {
    SpecialistDto findById(Long targetId);
    SpecialistDto findByUserId(Long targetId);

    List<SpecialistDto> findAll();
}
