package com.github.plantfern.foodDiary.specialists.api.apis;

import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface UserRelationApi {
    List<UserRelationDto> getByDiaryProfileIdInternal(Long diaryProfileId);
    List<UserRelationDto> getBySpecialistIdInternal(Long specialistId);

    boolean existsByDiaryProfileIdAndSpecialistIdInternal
            (Long diaryProfileId,
             Long specialistId);
}
