package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter

@Entity
@Table(name = "goal_nutrients")
public class GoalNutrientEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "goal_id",
            insertable = false,
            updatable = false
    )
    private GoalEntity goal;

    @Column(
            name = "goal_id",
            nullable = false
    )
    private Long goalId;

    @Column(name = "nutrient_id")
    private Long nutrientId;

    @Column(name = "amount")
    private Float amount;


    protected GoalNutrientEntity() {}

    public GoalNutrientEntity(
            Long goalId,
            Long nutrientId,
            Float amount
    ) {
        this.goalId = goalId;
        this.nutrientId = nutrientId;
        this.amount = amount;
    }
}