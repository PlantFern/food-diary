package com.github.plantfern.foodDiary.food.domain.views;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter
@Entity
@Immutable
@Table(name = "v_recipe_nutrients")
public class VRecipeNutrientEntity {

    @Id
    @Column(name = "recipeId")
    private Long recipeId;

    @Column(name = "nutrientId")
    private Long nutrientId;

    @Column(name = "recipeCode")
    private String recipeCode;

    @Column(name = "nutrientCode")
    private String nutrientCode;

    @Column(name = "amountPer100g")
    private Float amountPer100g;

    @Column(name = "unitCode")
    private String unitCode;

    protected VRecipeNutrientEntity() {}
}
