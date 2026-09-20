package com.github.plantfern.foodDiary.food.domain.views;


import org.hibernate.annotations.Immutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;


@Getter
@Entity
@Immutable
@Table(name = "v_recipes_basic")
public class VRecipeBasicEntity {

    @Id
    @Column(name = "recipeId")
    private Long recipeId;

    @Column(name = "recipeCode")
    private String recipeCode;

    @Column(name = "recipeName")
    private String recipeName;

    @Column(name = "recipeDescription")
    private String recipeDescription;

    @Column(name = "photoPath")
    private String photoPath;

    @Column(name = "totalWeightGrams")
    private Float totalWeightGrams;

    @Column(name = "isPublic")
    private Boolean isPublic;

    @Column(name = "createdById")
    private Long createdById;

    @Column(name = "entityStatusId")
    private Long entityStatusId;

    @Column(name = "entityStatusCode")
    private String entityStatusCode;

    protected VRecipeBasicEntity() {}
}
