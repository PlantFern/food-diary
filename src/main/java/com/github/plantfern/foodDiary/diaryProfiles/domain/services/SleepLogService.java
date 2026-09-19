package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.SleepLogApi;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.SleepLogEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.SleepLogRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@AllArgsConstructor
@Service
public class SleepLogService implements SleepLogApi {

    private final SleepLogRepository sleepLogRepository;
    private final DiaryProfileService diaryProfileService;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;


    @Transactional
    public void create(
            Long diaryProfileId,
            LocalDateTime beganAt,
            LocalDateTime endedAt
    ) {

        if(beganAt.isAfter(endedAt))
            throw new IllegalArgumentException("Sleep start cannot be after sleep end");

        var diaryProfile = diaryProfileService.findById(diaryProfileId);

        diaryProfilePolicy.ensureIsOwner(currentUser, diaryProfile.userId());

        sleepLogRepository.save(
                new SleepLogEntity(
                        diaryProfile.id(),
                        beganAt,
                        endedAt
                )
        );
    }

    @Transactional
    public void update(
            Long sleepLogId,
            LocalDateTime beganAt,
            LocalDateTime endedAt
    ) {

        if(beganAt.isAfter(endedAt))
            throw new IllegalArgumentException("Sleep start cannot be after sleep end");

        var sleepLog = sleepLogRepository
                .findById(sleepLogId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Sleep log with such id not found")
                );

        var diaryProfile = sleepLog.getDiaryProfile();

        diaryProfilePolicy.ensureIsOwner(currentUser, diaryProfile.getUserId());

        sleepLog.setBeganAt(beganAt);
        sleepLog.setEndedAt(endedAt);
        sleepLog.setDiaryProfileId(diaryProfile.getId());

        sleepLogRepository.save(sleepLog);
    }

    @Transactional
    public void delete(Long sleepLogId) {

        var sleepLog = sleepLogRepository
                .findById(sleepLogId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Sleep log with such id not found")
                );

        var diaryProfileId = sleepLog.getDiaryProfile().getUserId();

        diaryProfilePolicy.ensureIsOwner(currentUser, diaryProfileId);

        sleepLogRepository.delete(sleepLog);
    }


    @Transactional
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
