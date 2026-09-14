package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter

@Entity
@Table(
        name = "sleep_logs"
)
public class SleepLogEntity {

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

    @Column(name = "began_at", nullable = false)
    private LocalDateTime beganAt;

    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;

    @Column(
            name = "createdAt",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    protected SleepLogEntity() {}

    public SleepLogEntity(
            Long diaryProfileId,
            LocalDateTime beganAt,
            LocalDateTime endedAt
    ) {
        this.diaryProfileId = diaryProfileId;
        this.beganAt = beganAt;
        this.endedAt = endedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}