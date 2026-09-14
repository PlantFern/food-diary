package com.github.plantfern.foodDiary.food.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter

@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "parent_id",
            insertable = false,
            updatable = false
    )
    private CategoryEntity parent;

    @Column(
            name = "parent_id",
            nullable = true
    )
    private Long parentId;


    protected CategoryEntity() {}

    public CategoryEntity(String code, Long parentId) {
        this.code = code;
        this.parentId = parentId;
    }
}