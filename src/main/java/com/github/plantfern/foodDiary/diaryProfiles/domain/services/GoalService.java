package com.github.plantfern.foodDiary.diaryProfiles.domain.services;

import com.github.plantfern.foodDiary.diaryProfiles.api.apis.GoalApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.GoalMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.GoalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@AllArgsConstructor
@Service
public class GoalService implements GoalApi {


    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    @Override
    public GoalDto getActiveByDiaryProfile(Long diaryDiaryProfileId) {
        var goal = goalRepository
                    .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryDiaryProfileId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Any goal not found")
                );

        if (goal.getActualEndDate() == null)
            return goalMapper.toDto(goal);
        return null;
    }

    @Override
    public GoalDto getById(Long id) {
        return goalRepository
                .findById(id)
                .map(goalMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Goal with such id not founded")
                );
    }
}
