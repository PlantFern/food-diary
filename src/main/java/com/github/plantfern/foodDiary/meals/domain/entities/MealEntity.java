package com.github.plantfern.foodDiary.meals.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Table(name = "meals")
@SQLRestriction( "deleted_at is null" )
public class MealEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diary_profile_id", nullable = false)
    private Long diaryProfileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "meal_type_id",
            insertable = false,
            updatable = false
    )
    private MealTypeEntity mealType;

    @Column(name = "meal_type_id", nullable = false)
    private Long mealTypeId;

    @ManyToOne
    @JoinColumn(
            name = "generated_from_template_id",
            insertable = false,
            updatable = false
    )
    private MealTemplateEntity generatedFromTemplate;

    @Column(name = "generated_from_template_id", nullable = false)
    private Long generatedFromTemplateId;

    @Column(name = "photo_path")
    private String photoPath;

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


    protected MealEntity() { }

    public MealEntity(
            Long diaryProfileId,
            Long mealTypeId,
            Long generatedFromTemplateId,
            String photoPath
    ) {
        this.diaryProfileId = diaryProfileId;
        this.mealTypeId = mealTypeId;
        this.generatedFromTemplateId = generatedFromTemplateId;
        this.photoPath = photoPath;
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