package com.github.plantfern.foodDiary.common.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter

@Entity
@Table(name = "nutrient_units")
public class NutrientUnitEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "nutrientUnit"
    )
    List<NutrientEntity> nutrientEntityList;

    protected NutrientUnitEntity() {}

    public NutrientUnitEntity(String code) {
        this.code = code;
    }
}
