package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeListItemDto;
import com.github.plantfern.foodDiary.food.api.dto.RecipeListItemDto;
import com.github.plantfern.foodDiary.food.domain.views.VRecipeBasicEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface VRecipeBasicRepository extends JpaRepository<VRecipeBasicEntity, Long> {

    @Query("""
            select new com.github.plantfern.foodDiary.food.api.dto.PersonalizedRecipeListItemDto(
                        recipe.recipeId,
                        recipe.recipeName,
                        recipe.recipeDescription,
                        recipe.photoPath,
                        recipe.totalWeightGrams,
                        recipe.isPublic,
                        case when favorite.id is not null then true else false end
            )
            from VRecipeBasicEntity recipe
                left join FavoriteFoodEntity favorite
                            on favorite.itemId = recipe.recipeId
                            and favorite.itemType = com.github.plantfern.foodDiary.food.api.ItemType.RECIPE
                            and favorite.diaryProfileId = :diaryProfileId
            where recipe.entityStatusCode = 'ACTIVE'
                and (
                    recipe.isPublic = true
                        or recipe.createdById = :currentUserId
                    )
                and (
                    :query is null
                        or lower(recipe.recipeName) like lower(concat('%', cast(:query as string), '%'))
                        or lower(recipe.recipeDescription) like lower(concat('%', cast(:query as string), '%'))
                    )
                and ( :favorites = false or favorite.id is not null)
                and ( :onlyMy = false or recipe.createdById = :currentUserId)
                    order by case when favorite.id is not null then true else false end
            """)
    List<PersonalizedRecipeListItemDto> findPersonalizedList(
            @Param("diaryProfileId") Long diaryProfileId,
            @Param("currentUserId") Long currentUserId,
            @Param("query") @Nullable String query,
            @Param("onlyFavorites") boolean onlyFavorites,
            @Param("OnlyMy") boolean onlyMy
    );

    @Query("""
                select new com.github.plantfern.foodDiary.food.api.dto.RecipeListItemDto(
                    recipe.recipeId,
                    recipe.recipeName,
                    recipe.photoPath
                )
                from VRecipeBasicEntity recipe
                where (:query is null
                        or lower(recipe.recipeName) like lower(concat('%', cast(:query as string), '%'))
                        or lower(recipe.recipeDescription) like lower(concat('%', cast(:query as string), '%'))
                )
    """)
    List<RecipeListItemDto> findList(
            @Param("query") @Nullable String query
    );

    Optional<VRecipeBasicEntity> findByRecipeId(Long recipeId);
}
