package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.WeightLogApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.WeightLogEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.WeightLogMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.WeightLogRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@AllArgsConstructor
@Service
public class WeightLogService implements WeightLogApi {

    private final WeightLogRepository weightLogRepository;
    private final DiaryProfileService diaryProfileService;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;
    private final WeightLogMapper weightLogMapper;


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

        weightLogRepository.delete(weightLog);
    }


    @Transactional
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
