package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileHiddenNutrientEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.ProfileHiddenNutrientRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class ProfileHiddenNutrientService {

    private final ProfileHiddenNutrientRepository profileHiddenNutrientRepository;
    private final DiaryProfileService diaryProfileService;
    private final ProfileFeatureSettingsService profileFeatureSettingsService;


    public List<ProfileHiddenNutrientEntity> getAllByProfileFeatureSettingsId(
            Long profileFeatureSettingsId
    ) {
        profileFeatureSettingsService.getById(profileFeatureSettingsId);

        return profileHiddenNutrientRepository
                .findAllByProfileFeatureSettingsId(profileFeatureSettingsId);
    }
}
