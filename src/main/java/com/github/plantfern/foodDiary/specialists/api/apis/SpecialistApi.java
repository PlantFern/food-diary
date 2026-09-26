package com.github.plantfern.foodDiary.specialists.api.apis;


import com.github.plantfern.foodDiary.specialists.api.dto.SpecialistDto;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface SpecialistApi {
    SpecialistDto getById(Long targetId);
    SpecialistDto getByUserId(Long targetId);

    List<SpecialistDto> getAll();
}
