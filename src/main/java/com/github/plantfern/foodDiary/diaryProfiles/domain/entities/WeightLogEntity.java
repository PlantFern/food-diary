package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter

@Entity
@Table(name = "weight_logs")
public class WeightLogEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "diary_profile_id",
            insertable = false,
            updatable = false
    )
    private DiaryProfileEntity diaryProfile;

    @Column(
            name = "diary_profile_id",
            nullable = false
    )
    private Long diaryProfileId;

    @Column(name = "weight", nullable = false)
    private Float weight;

    @Column(
            name = "createdAt",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    protected WeightLogEntity() {}

    public WeightLogEntity(Long diaryProfileId, Float weight) {
        this.diaryProfileId = diaryProfileId;
        this.weight = weight;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}