package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.SleepLogApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.SleepLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.SleepLogEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.SleepLogMapper;
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
    private final SleepLogMapper sleepLogMapper;


    @Transactional
    public SleepLogDto create(
            Long diaryProfileId,
            LocalDateTime beganAt,
            LocalDateTime endedAt
    ) {

        if(beganAt.isAfter(endedAt))
            throw new IllegalArgumentException("Sleep start cannot be after sleep end");

        var diaryProfile = diaryProfileService.getById(diaryProfileId);

        diaryProfilePolicy.ensureIsOwner(currentUser, diaryProfile.getUserId());

        return sleepLogMapper.toDto(
                sleepLogRepository.save(
                        new SleepLogEntity(
                                diaryProfile.getId(),
                                beganAt,
                                endedAt
                        )
                )
        );
    }

    @Transactional
    public SleepLogDto update(
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

        return sleepLogMapper.toDto(
                sleepLogRepository.save(
                        sleepLog
                )
        );
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

    public SleepLogDto getById(Long sleepLogId) {

        var sleepLog = sleepLogRepository
                .findById(sleepLogId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Sleep log with such id not found")
                );

        diaryProfilePolicy.ensureCanGet(currentUser, sleepLog.getDiaryProfile().getUserId());

        return sleepLogMapper.toDto(sleepLog);
    }


    @Transactional
    public List<SleepLogDto> getByDiaryProfileIdAndDate(Long diaryProfileId, LocalDate date) {

        var localDateTimeEarlier = LocalDateTime.of(date, LocalTime.MIN);
        var localDateTimeLater = localDateTimeEarlier.plusDays(1);

        var foundSleepLogList = sleepLogRepository
                .findAllByDiaryProfileIdAndBeganAtIsBeforeAndEndedAtIsAfter(
                        diaryProfileId,
                        localDateTimeLater,
                        localDateTimeLater
                )
                .stream()
                .toList();

        diaryProfilePolicy.ensureCanGet(currentUser, foundSleepLogList.getFirst().getDiaryProfile().getUserId());

        return foundSleepLogList
                .stream()
                .map(sleepLogMapper::toDto)
                .toList();
    }

    public List<SleepLogDto> getByDiaryProfile(Long diaryProfileId) {

        var foundSleepLog = sleepLogRepository
                .findByDiaryProfileId(diaryProfileId)
                .stream()
                .toList();

        diaryProfilePolicy.ensureCanGet(
                currentUser,
                foundSleepLog.getFirst().getDiaryProfile().getUserId()
        );

        return foundSleepLog
                .stream()
                .map(sleepLogMapper::toDto)
                .toList();
    }

    public List<SleepLogDto> getByDiaryProfileAndPeriod(
            Long diaryProfileId,
            LocalDateTime startPeriod,
            LocalDateTime endPeriod
    ) {

        var foundSleepLogList = sleepLogRepository
                .findAllByDiaryProfileIdAndBeganAtLessThanEqualAndEndedAtGreaterThanEqual(
                        diaryProfileId,
                        startPeriod,
                        endPeriod
                );

        diaryProfilePolicy.ensureCanGet(
                currentUser
                , foundSleepLogList.getFirst().getDiaryProfile().getUserId()
        );

        return foundSleepLogList
                .stream()
                .map(sleepLogMapper::toDto)
                .toList();
    }

    @Override
    public List<SleepLogDto> getByDiaryProfileIdAndDateInternal(Long diaryProfileId, LocalDate interval) {

        var localDateTimeEarlier = LocalDateTime.of(interval, LocalTime.MIN);
        var localDateTimeLater = localDateTimeEarlier.plusDays(1);

        return sleepLogRepository
                .findAllByDiaryProfileIdAndBeganAtIsBeforeAndEndedAtIsAfter(
                        diaryProfileId,
                        localDateTimeLater,
                        localDateTimeLater
                )
                .stream()
                .map(sleepLogMapper::toDto)
                .toList();
    }
}
