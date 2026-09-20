package com.github.plantfern.foodDiary.diaryProfiles.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Setter
@Getter

@Entity
@Table(
        name = "goal_nutrients",
        uniqueConstraints = @UniqueConstraint(columnNames = {"goal_id", "nutrient_id"})
)
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


    // region Overrides methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoalNutrientEntity that)) return false;
        return Objects.equals(getGoalId(), that.getGoalId())
                && Objects.equals(getNutrientId(), that.getNutrientId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(goalId, nutrientId);
    }
    // endregion
}