package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.domain.ProfileFeatureSettingsMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileHiddenNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.ProfileHiddenNutrientRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class ProfileHiddenNutrientService {

    private final ProfileHiddenNutrientRepository profileHiddenNutrientRepository;
    private final DiaryProfileService diaryProfileService;
    private final ProfileFeatureSettingsService profileFeatureSettingsService;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;
    private final ProfileFeatureSettingsMapper profileFeatureSettingsMapper;


    public void addToSettings(Long profileFeatureSettingsId, Long nutrientId) {

        var profileFeatureSettings = profileFeatureSettingsService
                .getByIdInternal(profileFeatureSettingsId);

        diaryProfilePolicy.ensureIsOwner(currentUser, profileFeatureSettings.createdById());

        profileHiddenNutrientRepository.save(
                new ProfileHiddenNutrientEntity(
                        profileFeatureSettingsMapper
                                .toEntity(profileFeatureSettings),
                        nutrientId
                )
        );
    }

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
                                    profileFeatureSettingsEntity,
                                    nutrientId
                            )
                    )
                    .toList();

        profileHiddenNutrientRepository.saveAll(
                hiddenNutrientList
        );
    }

    public List<ProfileHiddenNutrientEntity> getAllByProfileFeatureSettingsId(
            Long profileFeatureSettingsId
    ) {
        profileFeatureSettingsService.getById(profileFeatureSettingsId);

        return profileHiddenNutrientRepository
                .findAllByProfileFeatureSettingsId(profileFeatureSettingsId);
    }
}
