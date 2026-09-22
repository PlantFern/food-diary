package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.food.api.DataSource;
import com.github.plantfern.foodDiary.food.api.EntityStatus;
import com.github.plantfern.foodDiary.food.api.apis.ProductApi;
import com.github.plantfern.foodDiary.food.domain.entities.ProductDataSourceEntity;
import com.github.plantfern.foodDiary.food.domain.entities.ProductEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.DataSourceRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.EntityStatusRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.ProductDataSourceRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.ProductRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class ProductService implements ProductApi {

    private final CurrentUser currentUser;
    private final ProductRepository productRepository;
    private final EntityStatusRepository entityStatusRepository;
    private final ProductDataSourceRepository productDataSourceRepository;
    private final DataSourceRepository dataSourceRepository;
    private final FoodPolicy foodPolicy;
    private final ProductNutrientService productNutrientService;
    private final FoodServingService foodServingService;

    public ProductService(CurrentUser currentUser, ProductRepository productRepository, EntityStatusRepository entityStatusRepository, ProductDataSourceRepository productDataSourceRepository, DataSourceRepository dataSourceRepository, FoodPolicy foodPolicy, ProductNutrientService productNutrientService, FoodServingService foodServingService) {
        this.currentUser = currentUser;
        this.productRepository = productRepository;
        this.entityStatusRepository = entityStatusRepository;
        this.productDataSourceRepository = productDataSourceRepository;
        this.dataSourceRepository = dataSourceRepository;
        this.foodPolicy = foodPolicy;
        this.productNutrientService = productNutrientService;
        this.foodServingService = foodServingService;
    }

    public Long create(
            String description,
            Long categoryId,
            String photoPath,
            DataSource dataSource,
            String externalId
    ) {

        var entityStatus = entityStatusRepository
                .findByCode(EntityStatus.DRAFT.name())
                .orElseThrow(
                        () -> new EntityNotFoundException("Entity status with such code not found")
                );

        var foundDataSource = dataSourceRepository
                .findByCode(dataSource.name())
                .orElseThrow(
                        () -> new EntityNotFoundException("Data source with such code not found")
                );

        var savedProduct = productRepository.save(
                new ProductEntity(
                        description,
                        categoryId,
                        photoPath,
                        false,
                        entityStatus.getId(),
                        currentUser.requireId()
                )
        );

        productDataSourceRepository.save(
                new ProductDataSourceEntity(
                        savedProduct.getId(),
                        foundDataSource.getId(),
                        externalId
                )
        );

        savedProduct.assignCode(DataSource.valueOf(foundDataSource.getCode()));

        return productRepository
                .save(savedProduct)
                .getId();
    }

    public void activateProduct(Long productId) {

        foodPolicy.ensureModeration(currentUser);

        var foundProduct = getById(productId);

        var entityStatus = entityStatusRepository
                .findByCode(EntityStatus.ACTIVE.name())
                .orElseThrow(
                        () -> new EntityNotFoundException("Entity status with such code not found")
                );

        foundProduct.setEntityStatusId(entityStatus.getId());
        foundProduct.setPublic(true);

        productRepository.save(foundProduct);
    }

    public void archive(Long productId) {

        foodPolicy.ensureModeration(currentUser);

        var foundProduct = getById(productId);

        var entityStatus = entityStatusRepository
                .findByCode(EntityStatus.ARCHIVED.name())
                .orElseThrow(
                        () -> new EntityNotFoundException("Entity status with such code not found")
                );

        foundProduct.setEntityStatusId(entityStatus.getId());

        productRepository.save(foundProduct);
    }


    ProductEntity getById(Long id){
        return productRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Product with such id not found")
                );
    }

    public boolean existsById(Long productId) {
        return productRepository.existsById(productId);
    }


    // region Override

    @Override
    public Long createNutrientRecordingProduct(
            String description,
            Float gramWeight,
            Map<Long, Float> nutrients
    ) {

        var product = this.create(
                description,
                6L,
                "",
                DataSource.NUTRIENT_RECORDING,
                ""
        );

        productNutrientService.addAllToProduct(product, nutrients);

        return foodServingService.createForProduct(
                product,
                1L,
                gramWeight,
                1L,
                null
        ).id();
    }
    // end region
}
