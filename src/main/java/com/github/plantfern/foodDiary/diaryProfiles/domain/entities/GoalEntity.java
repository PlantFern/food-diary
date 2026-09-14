package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter

@Entity
@Table(name = "goals")
public class GoalEntity {

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

    @Column(name = "planned_weight")
    private Long plannedWeight;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;

    @Column(name = "created_by", nullable = false)
    private Long createdById;

    @Column(
            name = "createdAt",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;


    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "goalNutrient")
    private List<GoalNutrientEntity> goalNutrientList;


    protected GoalEntity() {}

    public GoalEntity(
            DiaryProfileEntity diaryProfile,
            Long plannedWeight,
            LocalDate startDate,
            LocalDate actualEndDate,
            LocalDate plannedEndDate,
            Long createdById
    ) {
        this.diaryProfile = diaryProfile;
        this.plannedWeight = plannedWeight;
        this.startDate = startDate;
        this.actualEndDate = actualEndDate;
        this.plannedEndDate = plannedEndDate;
        this.createdById = createdById;
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

    public void setDeletedDate() {
        if (deletedAt == null) {
            deletedAt = LocalDateTime.now();
        }
    }
}