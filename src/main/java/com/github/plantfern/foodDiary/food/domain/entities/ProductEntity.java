package com.github.plantfern.foodDiary.food.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter

@Entity
@Table( name = "products")
public class ProductEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 40, unique = true, nullable = false)
    private String code;

    @Column(name = "description", length = 150)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            insertable = false,
            updatable = false
    )
    private CategoryEntity category;

    @Column(
            name = "category_id"
    )
    private Long categoryId;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "is_public")
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entity_status_id",
            insertable = false,
            updatable = false
    )
    private EntityStatusEntity entityStatus;
    
    @Column (name = "entity_status_id")
    private Long entityStatusId;

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


    protected ProductEntity() {}

    public ProductEntity(String code, String description, Long categoryId, String photoPath, boolean isPublic, Long entityStatusId, Long createdById) {
        this.code = code;
        this.description = description;
        this.categoryId = categoryId;
        this.photoPath = photoPath;
        this.isPublic = isPublic;
        this.entityStatusId = entityStatusId;
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