package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.api.dto.PersonalizedProductListItemDto;
import com.github.plantfern.foodDiary.food.api.dto.ProductListItemDto;
import com.github.plantfern.foodDiary.food.api.dto.ShortPersonalizedProductDetailDto;
import com.github.plantfern.foodDiary.food.domain.views.VProductBasicEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface VProductBasicRepository extends JpaRepository<VProductBasicEntity, Long> {

    @Query("""
        select new com.github.plantfern.foodDiary.food.api.dto.PersonalizedProductListItemDto(
            product.productId,
            product.productCode,
            product.productDescription,
            product.photoPath,
            product.categoryCode,
            product.dataSourceCode,
            nutrient.amountPer100g,
            case when favorite.id is not null then true else false end
        )
        from VProductBasicEntity product
        left join VProductNutrientEntity nutrient
            on nutrient.productId = product.productId
           and nutrient.nutrientId = :mainNutrientId
        left join FavoriteFoodEntity favorite
            on favorite.itemId = product.productId
           and favorite.itemType = com.github.plantfern.foodDiary.food.api.ItemType.PRODUCT
           and favorite.diaryProfileId = :diaryProfileId
        where product.entityStatusCode = 'ACTIVE'
          and (
                (
                    (product.dataSourceCode is null or product.dataSourceCode <> 'NUTRIENT_RECORDING')
                    and (
                        product.isPublic = true
                        or product.createdById = :currentUserId
                    )
                )
                or 
                (
                    product.dataSourceCode = 'NUTRIENT_RECORDING'
                    and product.createdById = :currentUserId
                )
                    and (:query is null
                        or lower(product.productDescription) like lower(concat('%', cast(:query as string), '%'))
                        or lower(product.productCode) like lower(concat('%', cast(:query as string), '%')))
                    and (:categoryId is null or product.categoryId = :categoryId)
                    and (:onlyFavorites = false or favorite.id is not null)
                    and (:onlyMy = false or product.createdById = :currentUserId)
          )
        order by
            case when favorite.id is not null then 0 else 1 end
                    
        """)
    Page<PersonalizedProductListItemDto> findPersonalizedList(
            @Param("diaryProfileId") Long diaryProfileId,
            @Param("mainNutrient") Long mainNutrientId,
            @Param("currentUserId") Long currentUserId,
            @Param("query") @Nullable String query,
            @Param("categoryId") @Nullable Long categoryId,
            @Param("onlyFavorites") boolean onlyFavorites,
            @Param("onlyMy") boolean onlyMy,
            Pageable pageable
    );

    @Query("""
        select new com.github.plantfern.foodDiary.food.api.dto.ProductListItemDto(
            product.productId,
            product.productCode,
            product.productDescription,
            product.photoPath,
            product.categoryCode,
            product.dataSourceCode
        )
        from VProductBasicEntity product
        where :query is null
                    or lower(product.productDescription) like lower(concat('%', cast(:query as string), '%'))
                    or lower(product.productCode) like lower(concat('%', cast(:query as string), '%'))
                and (:categoryId is null or product.categoryId = :categoryId)
        """)
    Page<ProductListItemDto> findList(
            @Param("query") @Nullable String query,
            @Param("categoryId") @Nullable Long categoryId,
            Pageable pageable
    );

    @Query("""
        select new com.github.plantfern.foodDiary.food.api.dto.ShortPersonalizedProductDetailDto(
            product.productId,
            product.productCode,
            product.productDescription,
            product.photoPath,
            product.categoryCode,
            product.entityStatusCode,
            product.dataSourceCode,
            product.isPublic,
            case when favorite.id is not null then true else false end
                    )
        
        from VProductBasicEntity product
        left join FavoriteFoodEntity favorite
            on favorite.itemId = product.productId
           and favorite.itemType = com.github.plantfern.foodDiary.food.api.ItemType.PRODUCT
           and favorite.diaryProfileId = :diaryProfileId
        where   product.productId = :productId and
                product.entityStatusCode = 'ACTIVE'
          and (
                (
                    (product.dataSourceCode is null or product.dataSourceCode <> 'NUTRIENT_RECORDING')
                    and (
                        product.isPublic = true
                        or product.createdById = :currentUserId
                    )
                )
                or
                (
                    product.dataSourceCode = 'NUTRIENT_RECORDING'
                    and product.createdById = :currentUserId
                )
          )
        """)
    Optional<ShortPersonalizedProductDetailDto> findByProductIdPersonalized(
            @Param("diaryProfileId") Long diaryProfileId,
            @Param("productId") Long productId,
            @Param("currentUserId") Long currentUserId
    );

    Optional<VProductBasicEntity> findByProductId(Long productId);
}
