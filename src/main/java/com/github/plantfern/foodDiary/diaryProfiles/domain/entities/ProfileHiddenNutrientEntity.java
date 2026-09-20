package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter

@Entity
@Table(
        name = "profile_hidden_nutrients",
        uniqueConstraints = @UniqueConstraint(columnNames = {"profile_feature_setting_id", "nutrient_id"})
)
public class ProfileHiddenNutrientEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn(
            name = "profile_feature_setting_id",
            insertable=false,
            updatable=false
    )
    private ProfileFeatureSettingsEntity profileFeatureSettings;

    @Column(
            name = "profile_feature_setting_id",
            nullable = false
    )
    private Long profileFeatureSettingId;

    @Column( name = "nutrient_id" )
    private Long nutrientId;


    protected ProfileHiddenNutrientEntity() {}

    public ProfileHiddenNutrientEntity(
        Long profileFeatureSettingId,
        Long nutrientId
    ) {
        this.profileFeatureSettingId = profileFeatureSettingId;
        this.nutrientId = nutrientId;
    }


    // region Overrides methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileHiddenNutrientEntity that)) return false;
        return Objects.equals(getProfileFeatureSettingId(), that.getProfileFeatureSettingId())
                && Objects.equals(getNutrientId(), that.getNutrientId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileFeatureSettingId, nutrientId);
    }
    // endregion
}