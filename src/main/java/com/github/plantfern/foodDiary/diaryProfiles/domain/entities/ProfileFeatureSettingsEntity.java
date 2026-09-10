package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter

@Entity
@Table(
       name = "profile_feature_settings"
)
public class ProfileFeatureSettingsEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "diary_profile_id", nullable = false )
    private DiaryProfileEntity diaryProfileEntity;

    @Column(
            name = "show_sleep",
            nullable=false
    )
    private Boolean showSleep = true;

    @Column(
            name = "show_sleep_logs",
            nullable = false
    )
    private Boolean showSleepLogs = true;

    @Column(
            name = "show_weight",
            nullable = false
    )
    private Boolean showWeight = true;

    @Column(
            name = "show_weight_logs",
            nullable = false
    )
    private Boolean showWeightLogs = true;

    @Column(
            name = "show_allergens_warning",
            nullable = false
    )
    private Boolean showAllergensWarning = true;

    @Column(
            name = "created_by",
            nullable = false
    )
    private Long createdBy;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;


    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "profileFeatureSettings"
    )
    private List<ProfileHiddenNutrientEntity> profileHiddenNutrientEntityList;


    protected ProfileFeatureSettingsEntity() {}

    public ProfileFeatureSettingsEntity(
            DiaryProfileEntity diaryProfileEntity,
            Boolean showSleep,
            Boolean showSleepLogs,
            Boolean showWeight,
            Boolean showWeightLogs,
            Boolean showAllergensWarning,
            Long createdBy
            ) {
        this.diaryProfileEntity = diaryProfileEntity;
        this.showSleep = showSleep;
        this.showSleepLogs = showSleepLogs;
        this.showWeight = showWeight;
        this.showWeightLogs = showWeightLogs;
        this.showAllergensWarning = showAllergensWarning;
        this.createdBy = createdBy;
    }


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setExpiredDate() {
        if (expiredAt == null) {
            expiredAt = LocalDateTime.now();
        }
    }
}