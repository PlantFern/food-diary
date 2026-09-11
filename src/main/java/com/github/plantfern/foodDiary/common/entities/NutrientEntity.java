package com.github.plantfern.foodDiary.common.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@Entity
@Table( name = "nutrients" )
public class NutrientEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @JoinColumn(name = "unit_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private NutrientUnitEntity nutrientUnit;

    @Column(
            name = "unit_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Long nutrientUnitId;


    protected NutrientEntity() {}

    public NutrientEntity (String code, NutrientUnitEntity nutrientUnit) {
        this.code = code;
        this.nutrientUnit = nutrientUnit;
    }
}
