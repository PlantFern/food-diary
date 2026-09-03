package com.github.plantfern.foodDiary.diaryProfiles.api;


import com.github.plantfern.foodDiary.diaryProfiles.domain.dto.DiaryProfileDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DiaryProfileApi {
    DiaryProfileDto findById(Long id);

    List<DiaryProfileDto> findAllById(Collection<Long> ids);
}
