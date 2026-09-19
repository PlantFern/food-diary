package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter

@Entity
@Table(name = "goals")
@SQLRestriction("deleted_at is null")
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
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "goal",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<GoalNutrientEntity> goalNutrientList;


    protected GoalEntity() {}

    public GoalEntity(
            Long diaryProfileId,
            Long plannedWeight,
            LocalDate startDate,
            LocalDate plannedEndDate,
            Long createdById
    ) {
        this.diaryProfileId = diaryProfileId;
        this.plannedWeight = plannedWeight;
        this.startDate = startDate;
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

    public void setActualEndedDay() {
        if (actualEndDate == null) {
            actualEndDate = LocalDate.now();
        }
    }
}