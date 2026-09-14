package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.WeightLogApi;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.WeightLogEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.WeightLogRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
public class WeightLogService implements WeightLogApi {

    private final WeightLogRepository weightLogRepository;

    public WeightLogService(WeightLogRepository weightLogRepository) {
        this.weightLogRepository = weightLogRepository;
    }

    public WeightLogEntity getLatestByDiaryProfileId(Long diaryProfileId) {
        return weightLogRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Weight log for such diary profile id not found")
                );
    }

    public List<WeightLogEntity> getByDiaryProfileId(Long diaryProfileId) {
        return weightLogRepository.findByDiaryProfileId(diaryProfileId);
    }
}
