package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileFeatureSettingsEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.ProfileFeatureSettingsRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.UserApi;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ProfileFeatureSettingsService {

    private final DiaryProfileService diaryProfileService;
    private final ProfileFeatureSettingsRepository profileFeatureSettingsRepository;
    private final UserApi userApi;
    private final CurrentUser currentUser;
    private final DiaryProfilePolicy diaryProfilePolicy;

    @Autowired
    public ProfileFeatureSettingsService(
            DiaryProfileService diaryProfileService,
            ProfileFeatureSettingsRepository profileFeatureSettingsRepository,
            UserApi userApi,
            CurrentUser currentUser, DiaryProfilePolicy diaryProfilePolicy) {
        this.diaryProfileService = diaryProfileService;
        this.profileFeatureSettingsRepository = profileFeatureSettingsRepository;
        this.userApi = userApi;
        this.currentUser = currentUser;
        this.diaryProfilePolicy = diaryProfilePolicy;
    }

    public ProfileFeatureSettingsEntity getFirstByDiaryProfileIdOrderByCreatedAtDesc(
            Long diaryProfileId
    ) {

        var diaryProfile = diaryProfileService.findById(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfile.userId());

        return profileFeatureSettingsRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .orElseThrow(
                        () -> new EntityNotFoundException("No profile feature settings for such diaryProfile")
                );
    }

    public List<ProfileFeatureSettingsEntity> getAllByDiaryProfileId(
            Long diaryProfileId
    ) {

        var diaryProfile = diaryProfileService.findById(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfile.userId());

        return profileFeatureSettingsRepository
                .findAllByDiaryProfileId(diaryProfileId)
                .stream()
                .toList();
    }

    public List<ProfileFeatureSettingsEntity> getAllByCreatedById(
            Long userId
    ) {

        if(!userApi.existsById(userId))
            throw new EntityNotFoundException("No user with such id");

        var diaryProfileIdList = profileFeatureSettingsRepository
                .findAllDiaryProfileIdByCreatedById(userId)
                .stream()
                .toList();

        diaryProfilePolicy.ensureCanGetAll(
                currentUser,
                diaryProfileService
                        .getOwnerUserIdList(diaryProfileIdList)
        );

        return profileFeatureSettingsRepository
                .findAllByCreatedById(userId)
                .stream()
                .toList();
    }

    public List<ProfileFeatureSettingsEntity> findAllByCreatedByIdAndDiaryProfileId(
            Long userId,
            Long diaryProfileId
    ) {

        if(!userApi.existsById(userId))
            throw new EntityNotFoundException("No user with such id");

        var settings = profileFeatureSettingsRepository.findAllByCreatedById(userId);
        if (settings.isEmpty()) {
            return List.of();
        }

        var diaryProfileIds = settings.stream()
                .map(ProfileFeatureSettingsEntity::getDiaryProfileId)
                .distinct()
                .toList();

        diaryProfilePolicy.ensureCanGetAll(
                currentUser,
                diaryProfileService.getOwnerUserIdList(diaryProfileIds)
        );

        return settings;
    }

    public List<ProfileFeatureSettingsEntity> getAllByExpiredAtBetweenOrCreatedAtLessThanAndDiaryProfileId(
            LocalDateTime lowerBound,
            LocalDateTime upperBound,
            Long diaryProfileId
    ) {
        if (lowerBound.isAfter(upperBound))
            throw new IllegalArgumentException(
                    "Lower bound can't be greater than upper bound"
            );

        var diaryProfile = diaryProfileService.findById(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfile.userId());

        return profileFeatureSettingsRepository
                .findAllByExpiredAtGreaterThanEqualOrCreatedAtLessThanAndDiaryProfileId(
                        lowerBound,
                        upperBound,
                        diaryProfileId
                )
                .stream()
                .toList();
    }
}
