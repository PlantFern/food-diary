package com.github.plantfern.foodDiary.food.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter

@Entity
@Table( name = "recipe_components")
public class RecipeComponentEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recipe_id",
            insertable = false,
            updatable = false
    )
    private RecipeEntity recipe;

    @Column(
            name = "recipe_id",
            nullable = false
    )
    private  Long recipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_serving_id",
            insertable = false,
            updatable = false
    )
    private FoodServingEntity productServing;

    @Column(
            name = "product_serving_id",
            nullable = false
    )
    private  Long productServingId;

    @Column(name = "amount", nullable = false)
    private Float amount;


    protected RecipeComponentEntity () {}

    public RecipeComponentEntity(Long recipeId, Long productServingId, Float amount) {
        this.recipeId = recipeId;
        this.productServingId = productServingId;
        this.amount = amount;
    }
}
