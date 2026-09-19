package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.WeightLogApi;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.WeightLogEntity;
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


    @Transactional
    public void create(
            Long diaryProfileId,
            Float weight
    ) {

        if(weight <= 0)
            throw new IllegalArgumentException("Weight cannot be less than or equal to 0");

        var diaryProfile = diaryProfileService
                .getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureIsOwner(currentUser, diaryProfile.userId());

        weightLogRepository.save(
                new WeightLogEntity(
                        diaryProfileId,
                        weight
                )
        );
    }

    @Transactional
    public void update(
            Long weightLogId,
            Long diaryProfileId,
            Float weight
    ) {

        if(weight <= 0)
            throw new IllegalArgumentException("Weight cannot be less than or equal to 0");

        var diaryProfile = diaryProfileService.getById(diaryProfileId);

        var weightLog = weightLogRepository.findById(weightLogId).orElseThrow(
                () -> new EntityNotFoundException("Weight log with such id not found")
        );

        diaryProfilePolicy.ensureIsOwner(
                currentUser,
                weightLog.getDiaryProfile().getUserId()
        );

        weightLog.setDiaryProfileId(diaryProfile.getId());
        weightLog.setWeight(weight);

        weightLogRepository.save(weightLog);
    }

    @Transactional
    public void delete(Long weightLogId) {

        var weightLog = weightLogRepository.findById(weightLogId).orElseThrow(
                () -> new EntityNotFoundException("Weight log with such id not found")
        );

        diaryProfilePolicy.ensureIsOwner(
                currentUser,
                weightLog.getDiaryProfile().getUserId()
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
