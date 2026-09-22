package com.github.plantfern.foodDiary.meals.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;


@Setter
@Getter
@Entity
@Table(name = "food_records")
public class MealFoodRecordEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serving_id", nullable = false)
    private Long servingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "meal_data_id",
            insertable = false,
            updatable = false
    )
    private MealEntity meal;

    @Column(name = "meal_data_id", nullable = false)
    private Long mealId;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(
            name = "eaten_at",
            nullable = false,
            updatable = false
    )
    private LocalTime eatenAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    protected MealFoodRecordEntity() {}

    public MealFoodRecordEntity(Long servingId, Long mealId, Float amount, LocalTime eatenAt) {
        this.servingId = servingId;
        this.mealId = mealId;
        this.amount = amount;
        this.eatenAt = eatenAt;
    }


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}