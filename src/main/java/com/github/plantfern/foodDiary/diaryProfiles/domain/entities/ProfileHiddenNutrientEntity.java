package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table( name = "profile_hidden_nutrients" )
public class ProfileHiddenNutrientEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "profile_feature_setting_id" )
    private ProfileFeatureSettingsEntity profileFeatureSettings;

    @Column(
            name = "profile_feature_setting_id",
            nullable = false,
            insertable=false,
            updatable=false
    )
    private Long profileFeatureSettingId;

    @Column( name = "nutrient_id" )
    private Long nutrientId;


    protected ProfileHiddenNutrientEntity() {}

    public ProfileHiddenNutrientEntity(
        ProfileFeatureSettingsEntity profileFeatureSetting,
        Long nutrientId
    ) {
        this.profileFeatureSettings = profileFeatureSetting;
        this.nutrientId = nutrientId;
    }
}