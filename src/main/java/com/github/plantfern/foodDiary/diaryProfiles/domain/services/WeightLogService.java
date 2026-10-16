package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.CommentableType;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.WeightLogApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.WeightLogEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.WeightLogMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.WeightLogRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@AllArgsConstructor
@Service
public class WeightLogService implements WeightLogApi {

    private final WeightLogRepository weightLogRepository;
    private final DiaryProfileService diaryProfileService;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;
    private final WeightLogMapper weightLogMapper;
    private final DiaryCommentService diaryCommentService;


    @Transactional
    public WeightLogDto create(
            Long diaryProfileId,
            Float weight
    ) {

        if(weight <= 0)
            throw new IllegalArgumentException("Weight cannot be less than or equal to 0");

        var diaryProfile = diaryProfileService
                .getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureIsOwner(currentUser, diaryProfile.userId());

        return weightLogMapper
                .toDto(weightLogRepository.save(
                        new WeightLogEntity(
                                diaryProfileId,
                                weight
                        )
                )
        );
    }

    @Transactional
    public WeightLogDto update(
            Long weightLogId,
            Float weight
    ) {

        if(weight <= 0)
            throw new IllegalArgumentException("Weight cannot be less than or equal to 0");

        var weightLog = weightLogRepository.findById(weightLogId).orElseThrow(
                () -> new EntityNotFoundException("Weight log with such id not found")
        );

        var diaryProfileUser = diaryProfileService
                .getOwnerUserIdInternal(weightLog.getDiaryProfileId());

        diaryProfilePolicy.ensureIsOwner(
                currentUser,
                diaryProfileUser
        );

        weightLog.setWeight(weight);

        return weightLogMapper.toDto(weightLogRepository.save(weightLog));
    }

    @Transactional
    public void delete(Long weightLogId) {

        var weightLog = weightLogRepository.findById(weightLogId).orElseThrow(
                () -> new EntityNotFoundException("Weight log with such id not found")
        );

        var diaryProfileUser = diaryProfileService
                .getOwnerUserIdInternal(weightLog.getDiaryProfileId());

        diaryProfilePolicy.ensureIsOwner(
                currentUser,
                diaryProfileUser
        );

        diaryCommentService.deleteByCommentableInternal(CommentableType.WEIGHT_LOG, weightLogId);
        weightLogRepository.delete(weightLog);
    }


    @Transactional
    public WeightLogDto getLatestByDiaryProfileId(Long diaryProfileId) {

        var foundWeightLog = weightLogRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .map(weightLogMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Weight log for such diary profile id not found")
                );

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(foundWeightLog.diaryProfileId());

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return foundWeightLog;
    }

    public List<WeightLogDto> getByDiaryProfileId(Long diaryProfileId) {

        var foundWeightLogList = weightLogRepository.findByDiaryProfileId(diaryProfileId)
                .stream()
                .map(weightLogMapper::toDto)
                .toList();

        if (foundWeightLogList.isEmpty()) {
            var ownerId = diaryProfileService.getOwnerUserIdInternal(diaryProfileId);
            diaryProfilePolicy.ensureCanGet(currentUser, ownerId);
            return List.of();
        }

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(foundWeightLogList.getFirst().diaryProfileId());

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return foundWeightLogList;
    }

    public WeightLogDto getById(Long weightLogId){

        var found = weightLogRepository
                .findById(weightLogId)
                .map(weightLogMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("Weight log with such id not found")
                );

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(found.diaryProfileId());

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return found;
    }

    public List<WeightLogDto> getByDiaryProfileAndPeriod(
            Long diaryProfileId,
            LocalDate beginPeriod,
            LocalDate endPeriod
    ) {

        if(beginPeriod.isAfter(endPeriod))
            throw new IllegalArgumentException("Period cannot begin after it ends");

        var foundWeightLogList = weightLogRepository.findAllByDiaryProfileIdAndCreatedAtBetween(
                        diaryProfileId,
                        LocalDateTime.of(beginPeriod, LocalTime.MIN),
                        LocalDateTime.of(endPeriod, LocalTime.MIN)
                )
                .stream()
                .map(weightLogMapper::toDto)
                .toList();

        if (foundWeightLogList.isEmpty()) {
            var ownerId = diaryProfileService.getOwnerUserIdInternal(diaryProfileId);
            diaryProfilePolicy.ensureCanGet(currentUser, ownerId);
            return List.of();
        }

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(foundWeightLogList.getFirst().diaryProfileId());

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return foundWeightLogList;
    }

    @Override
    @Nullable
    public WeightLogDto getLatestByDiaryProfileIdInternal(Long diaryProfileId) {

        return weightLogRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .map(weightLogMapper::toDto)
                .orElse(null);
    }
}
