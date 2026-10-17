package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.common.domain.services.NutrientService;
import com.github.plantfern.foodDiary.diaryProfiles.api.apis.ProfileFeatureSettingsApi;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileHiddenNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.ProfileFeatureSettingsMapper;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.ProfileFeatureSettingsDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileFeatureSettingsEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.ProfileFeatureSettingsRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.specialists.api.apis.UserRelationApi;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.UserApi;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Service
@AllArgsConstructor
public class ProfileFeatureSettingsService implements ProfileFeatureSettingsApi {

    private final DiaryProfileService diaryProfileService;
    private final ProfileFeatureSettingsRepository profileFeatureSettingsRepository;
    private final UserApi userApi;
    private final CurrentUser currentUser;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final ProfileFeatureSettingsMapper profileFeatureSettingsMapper;
    private final NutrientService nutrientService;
    private final UserRelationApi userRelationApi;

    private void ensureNotLockedBySpecialist(Long diaryProfileId) {
        if (userRelationApi.hasActiveSpecialistControlInternal(diaryProfileId)) {
            throw new IllegalStateException(
                    "Settings are managed by your specialist and cannot be changed"
            );
        }
    }

    @Transactional
    public ProfileFeatureSettingsDto create(
            Long diaryProfileId,
            Boolean showSleep,
            Boolean showSleepLogs,
            Boolean showWeight,
            Boolean showWeightLogs,
            Boolean showAllergensWarning,
            Set<Long> hiddenNutrients
    ) {
        var currentUserId = currentUser.requireId();
        var diaryProfile = diaryProfileService.getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanWrite(currentUser, diaryProfile.userId());
        ensureNotLockedBySpecialist(diaryProfileId);

        if (hiddenNutrients != null && !hiddenNutrients.isEmpty()
                && !nutrientService.existsAllByIdIn(hiddenNutrients)) {
            throw new EntityNotFoundException("Not all nutrients found");
        }

        profileFeatureSettingsRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .ifPresent(
                        lastSettings -> {
                            lastSettings.setExpiredDate();
                            profileFeatureSettingsRepository.save(lastSettings);
                        }
                );

        var profileFeatureSettings = profileFeatureSettingsRepository
                .save(
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

        if (hiddenNutrients != null) {
            for (Long nutrientId : hiddenNutrients) {
                profileFeatureSettings.getProfileHiddenNutrientSet().add(
                        new ProfileHiddenNutrientEntity(profileFeatureSettings.getId(), nutrientId)
                );
            }
            profileFeatureSettingsRepository.save(profileFeatureSettings);
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
            Set<Long> hiddenNutrients
    ) {

        var oldSettings =
                profileFeatureSettingsRepository
                        .findById(profileFeatureSettingsId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("No profile feature settings with such id")
                        );

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(oldSettings.getDiaryProfileId());

        diaryProfilePolicy.ensureCanWrite(currentUser, diaryProfileUserId);
        diaryProfilePolicy.ensureCreatedBy(currentUser, oldSettings.getCreatedById());
        ensureNotLockedBySpecialist(oldSettings.getDiaryProfileId());

        if (hiddenNutrients != null && !hiddenNutrients.isEmpty()
                && !nutrientService.existsAllByIdIn(hiddenNutrients)) {
            throw new EntityNotFoundException("Not all nutrients found");
        }

        oldSettings.setExpiredDate();
        profileFeatureSettingsRepository.save(oldSettings);

        var newSettings = profileFeatureSettingsRepository.save(
                new ProfileFeatureSettingsEntity(
                        oldSettings.getDiaryProfileId(),
                        showSleep,
                        showSleepLogs,
                        showWeight,
                        showWeightLogs,
                        showAllergensWarning,
                        currentUser.requireId()
                )
        );

        if (hiddenNutrients != null) {
            for (Long nutrientId : hiddenNutrients) {
                newSettings.getProfileHiddenNutrientSet().add(
                        new ProfileHiddenNutrientEntity(newSettings.getId(), nutrientId)
                );
            }
            profileFeatureSettingsRepository.save(newSettings);
        }

        return profileFeatureSettingsMapper.toDto(newSettings);
    }

    @Transactional
    public ProfileFeatureSettingsDto reset(Long profileFeatureSettingsId) {

        var profileFeatureSettings =
                profileFeatureSettingsRepository
                        .findById(profileFeatureSettingsId)
                        .orElseThrow(
                                () -> new EntityNotFoundException("No profile feature settings with such id")
                        );

        var diaryProfileUserId = diaryProfileService
                .getOwnerUserIdInternal(profileFeatureSettings.getDiaryProfileId());

        diaryProfilePolicy.ensureCanWrite(currentUser, diaryProfileUserId);
        diaryProfilePolicy.ensureCreatedBy(currentUser, profileFeatureSettings.getCreatedById());
        ensureNotLockedBySpecialist(profileFeatureSettings.getDiaryProfileId());

        profileFeatureSettings.setExpiredDate();

        return profileFeatureSettingsMapper.toDto(
                profileFeatureSettingsRepository.save(profileFeatureSettings)
        );
    }


    @Transactional(readOnly = true)
    public ProfileFeatureSettingsDto getByIdDto(Long id) {

        var settings = profileFeatureSettingsRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Profile feature setting eith such id not found")
                );

        var diaryProfileUserId = diaryProfileService.getOwnerUserIdInternal(settings.getDiaryProfileId());

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return profileFeatureSettingsMapper.toDto(settings);
    }

    @Transactional(readOnly = true)
    public ProfileFeatureSettingsDto getLatestByDiaryProfileIdDto(Long diaryProfileId) {

        var diaryProfile = diaryProfileService.getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfile.userId());

        return profileFeatureSettingsRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .map(profileFeatureSettingsMapper::toDto)
                .orElseGet(() -> defaultSettings(diaryProfileId));
    }

    @Transactional(readOnly = true)
    public List<ProfileFeatureSettingsDto> getAllByDiaryProfileIdDto(Long diaryProfileId) {

        var diaryProfile = diaryProfileService.getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfile.userId());

        return profileFeatureSettingsRepository
                .findAllByDiaryProfileId(diaryProfileId)
                .stream()
                .map(profileFeatureSettingsMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfileFeatureSettingsDto> getAllByCreatedByIdAndDiaryProfileIdDto(
            Long userId,
            Long diaryProfileId
    ) {

        if (!userApi.existsByIdInternal(userId))
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

        return settings.stream().map(profileFeatureSettingsMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ProfileFeatureSettingsDto> getAllByExpiredAtBetweenOrCreatedAtLessThanAndDiaryProfileIdDto(
            LocalDateTime lowerBound,
            LocalDateTime upperBound,
            Long diaryProfileId
    ) {
        if (lowerBound.isAfter(upperBound))
            throw new IllegalArgumentException(
                    "Lower bound can't be greater than upper bound"
            );

        var diaryProfile = diaryProfileService.getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfile.userId());

        return profileFeatureSettingsRepository
                .findAllByExpiredAtGreaterThanEqualOrCreatedAtLessThanAndDiaryProfileId(
                        lowerBound,
                        upperBound,
                        diaryProfileId
                )
                .stream()
                .map(profileFeatureSettingsMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileFeatureSettingsDto getActiveByDiaryProfileInternal(Long diaryProfileId) {

        return profileFeatureSettingsRepository
                .findFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                .map(profileFeatureSettingsMapper::toDto)
                .orElseGet(() -> defaultSettings(diaryProfileId));
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

    /** Defaults when user has never saved settings: everything visible/enabled. */
    private static ProfileFeatureSettingsDto defaultSettings(Long diaryProfileId) {
        return new ProfileFeatureSettingsDto(
                null,
                diaryProfileId,
                true,
                true,
                true,
                true,
                true,
                null,
                null,
                null,
                Set.of()
        );
    }
}
