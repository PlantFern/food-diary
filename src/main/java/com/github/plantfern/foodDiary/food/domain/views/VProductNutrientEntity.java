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
    @Column(name = "productId")
    private Long productId;

    @Column(name = "nutrientId")
    private Long nutrientId;

    @Column(name = "productCode")
    private String productCode;

    @Column(name = "nutrientCode")
    private String nutrientCode;

    @Column(name = "amountPer100g")
    private Float amountPer100g;

    @Column(name = "unitCode")
    private String unitCode;

    protected VProductNutrientEntity() {}
}
