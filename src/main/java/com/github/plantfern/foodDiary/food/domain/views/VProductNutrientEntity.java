package com.github.plantfern.foodDiary.food.domain.views;


import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;


@Getter
@Immutable
@Entity
@Table(name = "v_product_nutrients")
public class VProductNutrientEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "nutrient_id")
    private Long nutrientId;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "nutrient_code")
    private String nutrientCode;

    @Column(name = "amount_per100g")
    private Float amountPer100g;

    @Column(name = "unit_code")
    private String unitCode;

    protected VProductNutrientEntity() {}
}
