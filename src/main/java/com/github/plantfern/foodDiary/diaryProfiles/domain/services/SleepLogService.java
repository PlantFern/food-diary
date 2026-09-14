package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.SleepLogApi;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.SleepLogEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.SleepLogRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
public class SleepLogService implements SleepLogApi {

    private final SleepLogRepository sleepLogRepository;

    SleepLogService(SleepLogRepository sleepLogRepository) {
        this.sleepLogRepository = sleepLogRepository;
    }

    List<SleepLogEntity> getByDiaryProfileIdAndDate(Long diaryProfileId, LocalDate date) {
        var localDateTimeEarlier = LocalDateTime.of(date, LocalTime.MIN);
        var localDateTimeLater = localDateTimeEarlier.plusDays(1);

        return sleepLogRepository
                .findAllByDiaryProfileIdAndBeganAtIsBeforeAndEndedAtIsAfter(
                        diaryProfileId,
                        localDateTimeLater,
                        localDateTimeLater
                )
                .stream()
                .toList();
    }

    List<SleepLogEntity> getByDiaryProfile(Long diaryProfileId) {
        return sleepLogRepository
                .findByDiaryProfileId(diaryProfileId)
                .stream()
                .toList();
    }
}
