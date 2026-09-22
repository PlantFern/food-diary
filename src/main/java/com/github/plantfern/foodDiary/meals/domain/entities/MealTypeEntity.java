package com.github.plantfern.foodDiary.meals.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "meal_types")
public class MealTypeEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    protected MealTypeEntity() { }

    public MealTypeEntity(String code) {
        this.code = code;
    }
}