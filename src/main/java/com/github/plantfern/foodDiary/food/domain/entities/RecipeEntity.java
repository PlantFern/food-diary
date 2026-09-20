package com.github.plantfern.foodDiary.food.domain.entities;


import com.github.plantfern.foodDiary.food.api.DataSource;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter

@Entity
@Table( name = "recipes")
public class RecipeEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 40, unique = true, nullable = false)
    private String code;

    @Column(name = "name", length = 100, unique = true, nullable = false)
    private String name;

    @Column(name = "description", unique = true, nullable = false)
    private String description;

    @Column(name = "recipe", unique = true, nullable = false)
    private String recipe;

    @Column(name = "photo_path", unique = true, nullable = false)
    private String photoPath;

    @Column(name = "total_weight_grams")
    private Float totalWeightGrams;

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


    protected RecipeEntity() {}

    public RecipeEntity(
            String name,
            String description,
            String recipe,
            String photoPath,
            Float totalWeightGrams,
            boolean isPublic,
            Long entityStatusId,
            Long createdById
    ) {
        this.name = name;
        this.description = description;
        this.recipe = recipe;
        this.photoPath = photoPath;
        this.totalWeightGrams = totalWeightGrams;
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

    public void assignCode() {
        if (code.isEmpty())
            code = String.format("%08d", id);
    }
}