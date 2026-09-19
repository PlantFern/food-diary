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

import java.util.List;


@Service
@AllArgsConstructor
public class ProfileHiddenNutrientService implements ProfileHiddenNutrientApi {

    private final ProfileHiddenNutrientRepository profileHiddenNutrientRepository;
    private final ProfileFeatureSettingsService profileFeatureSettingsService;
    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;
    private final ProfileFeatureSettingsMapper profileFeatureSettingsMapper;


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
