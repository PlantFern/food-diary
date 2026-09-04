package com.github.plantfern.foodDiary.specialists.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter

@Entity
@Table(name="specialists")
public class SpecialistEntity{
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name="user_id",
            nullable=false
    )
    private Long userId;

    @Column(
            name="is_active"
    )
    private Boolean isActive;

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


    public boolean isEnabled(){
        return deletedAt == null;
    }

    @OneToMany(
            mappedBy = "specialistId",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UserRelationEntity> relations;


    protected SpecialistEntity(){}

    public SpecialistEntity(Long user_id){
        this.userId = user_id;
        this.isActive = true;
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

    public void restore() {
        deletedAt = null;
    }
}