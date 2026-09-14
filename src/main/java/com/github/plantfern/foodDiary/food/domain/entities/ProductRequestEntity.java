package com.github.plantfern.foodDiary.food.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter

@Entity
@Table(name = "product_requests")
public class ProductRequestEntity{

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
            name = "front_photo_path",
            length = 200,
            nullable = false
    )
    private String frontPhotoPath;

    @Column(
            name = "product_composition_photo_path",
            length = 200,
            nullable = false
    )
    private String productCompositionPhotoPath;

    @Column(
            name = "product_nutrition_photo_path",
            length = 200,
            nullable = false
    )
    private String productNutritionPhotoPath;

    @Column(
            name = "barcode_photo_path",
            length = 200,
            nullable = false
    )
    private String barcodePhotoPath;

    @Column(name = "created_by")
    private Long createdById;

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


    protected ProductRequestEntity() {}

    public ProductRequestEntity(
            Long productId,
            String frontPhotoPath,
            String productCompositionPhotoPath,
            String productNutritionPhotoPath,
            String barcodePhotoPath,
            Long createdById
    ) {
        this.productId = productId;
        this.frontPhotoPath = frontPhotoPath;
        this.productCompositionPhotoPath = productCompositionPhotoPath;
        this.productNutritionPhotoPath = productNutritionPhotoPath;
        this.barcodePhotoPath = barcodePhotoPath;
        this.createdById = createdById;
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