package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.common.storage.FileService;
import com.github.plantfern.foodDiary.common.storage.PhotoPathResolver;
import com.github.plantfern.foodDiary.common.storage.StorageFolder;
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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@AllArgsConstructor
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
    private final FileService fileService;
    private final PhotoPathResolver photoPathResolver;


    @Transactional
    public Long create(
            String description,
            Long categoryId,
            Long photoPathId,
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

        String photoPath = photoPathResolver.fromFileId(photoPathId);

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

    @Transactional
    public String setPhoto(Long productId, MultipartFile file, StorageFolder storageFolder) {
        var product = getById(productId);
        boolean hasPhoto = product.getPhotoPath() != null && !product.getPhotoPath().isBlank();

        if (hasPhoto) {
            foodPolicy.ensureModeration(currentUser);
        } else {
            foodPolicy.ensureIsOwner(currentUser, product.getCreatedById());
        }

        var uploaded = fileService.upload(file, storageFolder);
        String path = "/api/files/" + uploaded.getId();
        product.setPhotoPath(path);
        productRepository.save(product);
        return path;
    }

    @Transactional
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

    @Transactional
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

    @Transactional(readOnly = true)
    ProductEntity getById(Long id){
        return productRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Product with such id not found")
                );
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long productId) {
        return productRepository.existsById(productId);
    }


    @Override
    @Transactional
    public Long createNutrientRecordingProduct(
            String description,
            Float gramWeight,
            Map<Long, Float> nutrients
    ) {

        var product = this.create(
                description,
                6L,
                null,
                DataSource.NUTRIENT_RECORDING,
                ""
        );

        productNutrientService.addAllToProduct(product, nutrients);

        return foodServingService.createForProduct(
                product,
                1F,
                gramWeight,
                1L,
                null
        ).id();
    }
}
