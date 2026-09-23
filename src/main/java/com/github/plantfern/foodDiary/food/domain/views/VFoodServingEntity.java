package com.github.plantfern.foodDiary.food.domain.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter

@Entity
@Immutable
@Table(
        name = "v_food_servings"
)
public class VFoodServingEntity {

    @Id
    @Column(name = "servingId")
    private Long servingId;

    @Column(name = "foodServingType")
    private String itemType;

    @Column(name = "foodServingItemId")
    private Long itemId;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "itemPhotoPath")
    private String itemPhotoPath;

    @Column(name = "servingAmount")
    private Float servingAmount;

    @Column(name = "gramWeight")
    private Float gramWeight;

    @Column(name = "servingUnitId")
    private Long servingUnitId;

    @Column(name = "servingUnitCode")
    private String servingUnitCode;

    protected VFoodServingEntity() { }
}
