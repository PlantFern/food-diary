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
        name = "v_item_nutrients"
)
public class VItemNutrientEntity {

    @Id
    @Column(name = "itemId")
    private Long itemId;

    @Column(name = "itemType")
    private String itemType;

    @Column(name = "nutrientId")
    private Long nutrientId;

    @Column(name = "nutrientCode")
    private String nutrientCode;

    @Column(name = "amountPer100g")
    private Float amountPer100g;

    @Column(name = "unitCode")
    private String unitCode;

    protected VItemNutrientEntity() {}
}
