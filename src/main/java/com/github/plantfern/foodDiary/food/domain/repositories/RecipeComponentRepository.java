package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.domain.entities.RecipeComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RecipeComponentRepository extends JpaRepository<RecipeComponentEntity, Long> {

    List<RecipeComponentEntity> findAllByRecipeId(Long recipeId);

    @Query("""
            select coalesce(sum(
                                    recipe_component.amount * (food_serving.gramWeight * food_serving.amount)
            ), 0)
            from RecipeComponentEntity recipe_component
            join FoodServingEntity food_serving on food_serving.id = recipe_component.productServingId
            where recipe_component.recipeId =: recipeId
            and food_serving.amount > 0
            """)
    Float sumWeightGramsByRecipeId(@Param("recipeId") Long recipeId);
}
