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
    private ProfileFeatureSettingsEntity profileFeatureSetting;

    @Column( name = "nutrient_id" )
    private Long nutrientId;


    protected ProfileHiddenNutrientEntity() {}

    public ProfileHiddenNutrientEntity(
        ProfileFeatureSettingsEntity profileFeatureSetting,
        Long nutrientId
    ) {
        this.profileFeatureSetting = profileFeatureSetting;
        this.nutrientId = nutrientId;
    }
}