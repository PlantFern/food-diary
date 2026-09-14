package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.ProfileHiddenNutrientApi;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.ProfileFeatureSettingsMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileHiddenNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.ProfileHiddenNutrientRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class ProfileHiddenNutrientService implements ProfileHiddenNutrientApi {

    private final ProfileHiddenNutrientRepository profileHiddenNutrientRepository;
    private final ProfileFeatureSettingsService profileFeatureSettingsService;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;
    private final ProfileFeatureSettingsMapper profileFeatureSettingsMapper;


    @Transactional
    public void addToSettings(Long profileFeatureSettingsId, Long nutrientId) {

        var profileFeatureSettings = profileFeatureSettingsService
                .getByIdInternal(profileFeatureSettingsId);

        diaryProfilePolicy.ensureCanWrite(currentUser, profileFeatureSettings.createdById());

        profileHiddenNutrientRepository.save(
                new ProfileHiddenNutrientEntity(
                        profileFeatureSettings.id(),
                        nutrientId
                )
        );
    }

    @Transactional
    public void addAllToSettings(Long profileFeatureSettingsId, List<Long> nutrientIds) {

        var profileFeatureSettings = profileFeatureSettingsService
                .getByIdInternal(profileFeatureSettingsId);
        var profileFeatureSettingsEntity = profileFeatureSettingsMapper
                .toEntity(profileFeatureSettings);

        diaryProfilePolicy.ensureIsOwner(currentUser, profileFeatureSettings.createdById());

        List<ProfileHiddenNutrientEntity> hiddenNutrientList =
            nutrientIds
                    .stream()
                    .map(nutrientId ->
                            new ProfileHiddenNutrientEntity(
                                    profileFeatureSettingsEntity.getId(),
                                    nutrientId
                            )
                    )
                    .toList();

        profileHiddenNutrientRepository.saveAll(
                hiddenNutrientList
        );
    }

    @Transactional
    public void removeFromSettings(
            Long profileHiddenNutrientId
    ) {

        var profileHiddenNutrient = profileHiddenNutrientRepository
                .findById(profileHiddenNutrientId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Profile hidden nutrient with such id not found")
                );

        diaryProfilePolicy.ensureCreatedBy(
                currentUser,
                profileHiddenNutrient.getProfileFeatureSettings().getCreatedById()
        );

        profileHiddenNutrientRepository.delete(profileHiddenNutrient);
    }

    @Transactional
    public void removeAllFromSettings(
            List<Long> profileHiddenNutrientIds
    ) {

        if (profileHiddenNutrientIds == null || profileHiddenNutrientIds.isEmpty()) {
            throw new IllegalArgumentException("Profile hidden nutrient list is empty");
        }

        var profileHiddenNutrients = profileHiddenNutrientRepository
                .findAllById(profileHiddenNutrientIds);

        if (profileHiddenNutrients.size() != profileHiddenNutrientIds.size()) {
            throw new EntityNotFoundException("Some profile hidden nutrients were not found");
        }

        var firstProfileHiddenNutrient = profileHiddenNutrients.getFirst();

        boolean allSameSettings = profileHiddenNutrients.stream()
                .allMatch(
                        profileHiddenNutrient ->
                                firstProfileHiddenNutrient.getProfileFeatureSettingId()
                                        .equals(profileHiddenNutrient.getProfileFeatureSettingId())
                );

        if (!allSameSettings) {
            throw new IllegalArgumentException(
                    "All hidden nutrients must belong to the same profile feature settings"
            );
        }

        diaryProfilePolicy.ensureCreatedBy(
                currentUser,
                firstProfileHiddenNutrient.getProfileFeatureSettings().getCreatedById()
        );

        profileHiddenNutrientRepository.deleteAll(profileHiddenNutrients);
    }


    @Transactional(readOnly = true)
    @Override
    public List<ProfileHiddenNutrientEntity> getAllByProfileFeatureSettingsIdInternal(
            Long profileFeatureSettingsId
    ) {
        profileFeatureSettingsService.getById(profileFeatureSettingsId);

        return profileHiddenNutrientRepository
                .findAllByProfileFeatureSettingsId(profileFeatureSettingsId);
    }
}
