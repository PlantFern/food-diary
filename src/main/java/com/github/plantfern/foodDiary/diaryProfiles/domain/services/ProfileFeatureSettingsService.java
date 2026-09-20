package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.common.services.NutrientService;
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
import java.util.Set;
import java.util.stream.Collectors;


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

        if(!nutrientService.existsAllByIdIn(hiddenNutrients))
            throw new EntityNotFoundException("Not all nutrients found");

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

        if(hiddenNutrients.isEmpty())
            return profileFeatureSettingsMapper.toDto(profileFeatureSettings);

        var profileFeatureSettingsId = profileFeatureSettings.getId();
        for(Long nutrientId : hiddenNutrients) {
            profileFeatureSettings.getProfileHiddenNutrientSet().add(
                    new ProfileHiddenNutrientEntity(profileFeatureSettingsId, nutrientId)
            );
        }

        return profileFeatureSettingsMapper.toDto(
                profileFeatureSettingsRepository.save(profileFeatureSettings)
        );
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

        if(!nutrientService.existsAllByIdIn(hiddenNutrients))
            throw new EntityNotFoundException("Not all nutrients found");

        oldSettings.setExpiredDate();

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
        profileFeatureSettingsRepository.save(oldSettings)

        if(hiddenNutrients.isEmpty())
            return profileFeatureSettingsMapper.toDto(newSettings);

        for(Long nutrientId : hiddenNutrients) {
            newSettings.getProfileHiddenNutrientSet().add(
                    new ProfileHiddenNutrientEntity(newSettings.getId(), nutrientId)
            );
        }

        return profileFeatureSettingsMapper.toDto(
            profileFeatureSettingsRepository
                    .save(newSettings)
        );
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

        profileFeatureSettings.setExpiredDate();

        return profileFeatureSettingsMapper.toDto(
                profileFeatureSettingsRepository.save(
                    profileFeatureSettings
            )
        );
    }


    @Transactional(readOnly = true)
    public ProfileFeatureSettingsEntity getById(Long id) {

        var settings = profileFeatureSettingsRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Profile feature setting eith such id not found")
                );

        var diaryProfileUserId = diaryProfileService.getOwnerUserIdInternal(settings.getDiaryProfileId());

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfileUserId);

        return settings;
    }

    @Transactional(readOnly = true)
    public ProfileFeatureSettingsEntity getFirstByDiaryProfileIdOrderByCreatedAtDesc(
            Long diaryProfileId
    ) {

        var diaryProfile = diaryProfileService.getByIdInternal(diaryProfileId);

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

        var diaryProfile = diaryProfileService.getByIdInternal(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(currentUser, diaryProfile.userId());

        return profileFeatureSettingsRepository
                .findAllByDiaryProfileId(diaryProfileId)
                .stream()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfileFeatureSettingsEntity> getAllByCreatedByIdAndDiaryProfileId(
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

        var diaryProfile = diaryProfileService.getByIdInternal(diaryProfileId);

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
