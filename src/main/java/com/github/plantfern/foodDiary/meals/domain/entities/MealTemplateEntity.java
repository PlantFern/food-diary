package com.github.plantfern.foodDiary.meals.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Setter
@Getter
@Entity
@Table(name = "meal_templates")
@SQLRestriction( "deleted_at is null" )
public class MealTemplateEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diary_profile_id", nullable = false)
    private Long diaryProfileId;

    @Column(name = "name")
    private String name;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;

    @Column(name = "frequency", nullable = false)
    private Long frequency;

    @Column(name = "started_at", nullable = false)
    private Long startedAt;

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


    protected MealTemplateEntity() { }

    public MealTemplateEntity(
            Long diaryProfileId,
            String name,
            LocalTime scheduledTime,
            Long frequency,
            Long startedAt
    ) {
        this.diaryProfileId = diaryProfileId;
        this.name = name;
        this.scheduledTime = scheduledTime;
        this.frequency = frequency;
        this.startedAt = startedAt;
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