package com.github.plantfern.foodDiary.food.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter

@Entity
@Table(name = "product_nutrients")
public class ProductNutrientEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            insertable = false,
            updatable = false
    )
    private ProductEntity product;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "nutrient_id", nullable = false)
    private Long nutrientId;

    @Column(name = "amount")
    private Float amount;


    protected ProductNutrientEntity() { }

    public ProductNutrientEntity(Float amount, Long nutrientId, Long productId) {
        this.amount = amount;
        this.nutrientId = nutrientId;
        this.productId = productId;
    }
}