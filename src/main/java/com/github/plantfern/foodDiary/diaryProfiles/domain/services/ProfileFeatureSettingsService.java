package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.ProfileFeatureSettingsApi;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileHiddenNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.ProfileFeatureSettingsMapper;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.ProfileFeatureSettingsDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileFeatureSettingsEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.ProfileFeatureSettingsRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.UserApi;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class ProfileFeatureSettingsService implements ProfileFeatureSettingsApi {

    private final DiaryProfileService diaryProfileService;
    private final ProfileFeatureSettingsRepository profileFeatureSettingsRepository;
    private final UserApi userApi;
    private final CurrentUser currentUser;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final ProfileFeatureSettingsMapper profileFeatureSettingsMapper;


    @Transactional
    public ProfileFeatureSettingsDto create(
            Long diaryProfileId,
            Boolean showSleep,
            Boolean showSleepLogs,
            Boolean showWeight,
            Boolean showWeightLogs,
            Boolean showAllergensWarning,
            List<Long> hiddenNutrients
    ) {
        var currentUserId = currentUser.requireId();
        var diaryProfile = diaryProfileService.findByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanWrite(currentUser, diaryProfile.userId());

        profileFeatureSettingsRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .ifPresent(
                        lastSettings -> {
                            lastSettings.setExpiredDate();
                            profileFeatureSettingsRepository.save(lastSettings);
                        }
                );

        var profileFeatureSettings = profileFeatureSettingsRepository.save(
                new ProfileFeatureSettingsEntity(
                        diaryProfile.id(),
                        showSleep,
                        showSleepLogs,
                        showWeight,
                        showWeightLogs,
                        showAllergensWarning,
                        currentUserId
                )
        );

        var profileFeatureSettingsId = profileFeatureSettings.getId();
        for(Long nutrientId : hiddenNutrients) {
            profileFeatureSettings.getProfileHiddenNutrientEntityList().add(
                    new ProfileHiddenNutrientEntity(profileFeatureSettingsId, nutrientId)
            );
        }

        return profileFeatureSettingsMapper.toDto(profileFeatureSettings);
    }

    @Transactional
    public ProfileFeatureSettingsDto update(
            Long profileFeatureSettingsId,
            Boolean showSleep,
            Boolean showSleepLogs,
            Boolean showWeight,
            Boolean showWeightLogs,
            Boolean showAllergensWarning,
            List<Long> hiddenNutrients
    ) {

        var profileFeatureSettings =
                profileFeatureSettingsRepository
                        .findById(profileFeatureSettingsId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("No profile feature settings with such id")
                        );

        diaryProfilePolicy.ensureCreatedBy(
                currentUser,
                profileFeatureSettings.getCreatedById()
        );

        profileFeatureSettings.setShowSleep(showSleep);
        profileFeatureSettings.setShowSleepLogs(showSleepLogs);
        profileFeatureSettings.setShowWeight(showWeight);
        profileFeatureSettings.setShowWeightLogs(showWeightLogs);
        profileFeatureSettings.setShowAllergensWarning(showAllergensWarning);

        profileFeatureSettings.getProfileHiddenNutrientEntityList().clear();

        for(Long nutrientId : hiddenNutrients) {
            profileFeatureSettings.getProfileHiddenNutrientEntityList().add(
                    new ProfileHiddenNutrientEntity(profileFeatureSettings.getId(), nutrientId)
            );
        }

        return profileFeatureSettingsMapper.toDto(
            profileFeatureSettingsRepository
                    .save(profileFeatureSettings)
        );
    }

    @Transactional
    public ProfileFeatureSettingsDto complete( Long profileFeatureSettingsId) {

        var currentUserId = currentUser.requireId();
        var profileFeatureSettings =
                profileFeatureSettingsRepository
                        .findById(profileFeatureSettingsId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("No profile feature settings with such id")
                        );
        var diaryProfile = diaryProfileService
                .findByIdInternal(profileFeatureSettings.getDiaryProfileId());

        diaryProfilePolicy.ensureCanWrite(
                currentUser,
                diaryProfile.userId()
        );

        profileFeatureSettings.setExpiredDate();

        return profileFeatureSettingsMapper.toDto(
                profileFeatureSettingsRepository.save(
                    profileFeatureSettings
            )
        );
    }

    @Transactional
    public void delete( Long profileFeatureSettingsId) {

        var currentUserId = currentUser.requireId();
        var profileFeatureSettings =
                profileFeatureSettingsRepository
                        .findById(profileFeatureSettingsId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("No profile feature settings with such id")
                        );
        var diaryProfile = diaryProfileService
                .findByIdInternal(profileFeatureSettings.getDiaryProfileId());

        diaryProfilePolicy.ensureCanWrite(
                currentUser,
                diaryProfile.userId()
        );

        profileFeatureSettings.getProfileHiddenNutrientEntityList().clear();

        profileFeatureSettingsRepository.delete(
                profileFeatureSettings
        );
    }


    @Transactional(readOnly = true)
    public ProfileFeatureSettingsEntity getById(Long id) {

        var settings = profileFeatureSettingsRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Profile feature setting eith such id not found")
                );

        diaryProfilePolicy.ensureCanGet(currentUser, settings.getDiaryProfile().getUserId());

        return settings;
    }

    @Transactional(readOnly = true)
    public ProfileFeatureSettingsEntity getFirstByDiaryProfileIdOrderByCreatedAtDesc(
            Long diaryProfileId
    ) {

        var diaryProfile = diaryProfileService.findByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfile.userId());

        return profileFeatureSettingsRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .orElseThrow(
                        () -> new EntityNotFoundException("No profile feature settings for such diaryProfile")
                );
    }

    @Transactional(readOnly = true)
    public List<ProfileFeatureSettingsEntity> getAllByDiaryProfileId(
            Long diaryProfileId
    ) {

        var diaryProfile = diaryProfileService.findByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfile.userId());

        return profileFeatureSettingsRepository
                .findAllByDiaryProfileId(diaryProfileId)
                .stream()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfileFeatureSettingsEntity> getAllByCreatedById(
            Long userId
    ) {

        if(!userApi.existsByIdInternal(userId))
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

    @Transactional(readOnly = true)
    public List<ProfileFeatureSettingsEntity> findAllByCreatedByIdAndDiaryProfileId(
            Long userId,
            Long diaryProfileId
    ) {

        if(!userApi.existsByIdInternal(userId))
            throw new EntityNotFoundException("No user with such id");

        var settings = profileFeatureSettingsRepository.findAllByCreatedByIdAndDiaryProfileId(userId, diaryProfileId);
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

    @Transactional(readOnly = true)
    public List<ProfileFeatureSettingsEntity> getAllByExpiredAtBetweenOrCreatedAtLessThanAndDiaryProfileId(
            LocalDateTime lowerBound,
            LocalDateTime upperBound,
            Long diaryProfileId
    ) {
        if (lowerBound.isAfter(upperBound))
            throw new IllegalArgumentException(
                    "Lower bound can't be greater than upper bound"
            );

        var diaryProfile = diaryProfileService.findByIdInternal(diaryProfileId);

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

    //region Internal methods

    @Override
    @Transactional(readOnly = true)
    public ProfileFeatureSettingsDto getActiveByDiaryProfileInternal(Long diaryProfileId) {

        return profileFeatureSettingsRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .map(profileFeatureSettingsMapper::toDto)
                .orElseThrow(
                        () -> new EntityNotFoundException("settings with such diary profile id not found")
                );
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileFeatureSettingsDto getByIdInternal(Long id) {

        var settings = profileFeatureSettingsRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("No ProfileFeatureSettings with such id")
                );

        return profileFeatureSettingsMapper.toDto(settings);
    }
    //endregion
}
