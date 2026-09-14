package com.github.plantfern.foodDiary.food.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter

@Entity
@Table(name = "branded_products")
public  class BrandedProductEntity {

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

    @Column(
            name = "product_id",
            nullable = false
    )
    private  Long productId;

    @Column(
            name = "barcode",
            nullable = false,
            length = 100
    )
    private  String barcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "base_serving_id",
            insertable = false,
            updatable = false
    )
    private FoodServingEntity baseServing;

    @Column(
            name = "base_serving_id",
            nullable = false
    )
    private  Long baseServingId;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    protected  BrandedProductEntity() {}

    public BrandedProductEntity(Long productId, String barcode, Long baseServingId) {
        this.productId = productId;
        this.barcode = barcode;
        this.baseServingId = baseServingId;
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

    public void softDelete() {
        if (deletedAt == null) {
            deletedAt = LocalDateTime.now();
        }
    }
}