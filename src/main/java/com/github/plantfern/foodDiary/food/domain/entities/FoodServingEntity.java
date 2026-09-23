package com.github.plantfern.foodDiary.food.domain.entities;


import com.github.plantfern.foodDiary.food.api.ItemType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter

@Entity
@Table(name = "food_servings")
public class FoodServingEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "gram_weight", nullable = false)
    private Float gramWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serving_unit_id",
            insertable = false,
            updatable = false
    )
    private ServingUnitEntity servingUnit;

    @Column(
            name = "serving_unit_id",
            nullable = false
    )
    private Long servingUnitId;

    @Column(name = "description")
    private String description;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    protected FoodServingEntity() {}

    public FoodServingEntity(
            Long itemId,
            ItemType itemType,
            Float amount,
            Float gramWeight,
            Long servingUnitId,
            String description
    ) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.amount = amount;
        this.gramWeight = gramWeight;
        this.servingUnitId = servingUnitId;
        this.description = description;
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