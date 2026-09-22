package com.github.plantfern.foodDiary.meals.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Table(name = "meal_template_food_records")
public class MealTemplateFoodRecordEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "template_id",
            insertable = false,
            updatable = false
    )
    private MealTemplateEntity mealTemplate;

    @Column(name = "template_id", nullable = false)
    private Long mealTemplateId;

    @Column(name = "serving_id", nullable = false)
    private Long servingId;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    protected MealTemplateFoodRecordEntity() { }

    public MealTemplateFoodRecordEntity(Long mealTemplateId, Long servingId, Float amount) {
        this.mealTemplateId = mealTemplateId;
        this.servingId = servingId;
        this.amount = amount;
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
}