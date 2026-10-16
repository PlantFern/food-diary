package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.domain.entities.FavoriteFoodEntity;
import com.github.plantfern.foodDiary.food.domain.entities.ProductEntity;
import com.github.plantfern.foodDiary.food.domain.entities.RecipeEntity;
import com.github.plantfern.foodDiary.food.domain.repositories.FavoriteFoodRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.ProductRepository;
import com.github.plantfern.foodDiary.food.domain.repositories.RecipeRepository;
import com.github.plantfern.foodDiary.food.domain.security.FoodPolicy;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@AllArgsConstructor
@Service
public class FavoriteFoodService {

    private final FavoriteFoodRepository favoriteFoodRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final DiaryProfileApi diaryProfileApi;
    private final FoodPolicy foodPolicy;
    private final CurrentUser currentUser;


    @Transactional
    public void add(Long diaryProfileId, ItemType itemType, Long itemId) {
        ensureOwner(diaryProfileId);
        ensureItemExists(itemType, itemId);

        if (favoriteFoodRepository.existsByDiaryProfileIdAndItemTypeAndItemId(
                diaryProfileId, itemType, itemId)) {
            return;
        }

        try {
            favoriteFoodRepository.save(
                    new FavoriteFoodEntity(itemType, itemId, diaryProfileId)
            );
        } catch (DataIntegrityViolationException ignored) {
        }
    }

    @Transactional
    public void remove(Long diaryProfileId, ItemType itemType, Long itemId) {
        ensureOwner(diaryProfileId);
        favoriteFoodRepository.deleteByDiaryProfileIdAndItemTypeAndItemId(
                diaryProfileId, itemType, itemId
        );
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(Long diaryProfileId, ItemType itemType, Long itemId) {
        ensureOwner(diaryProfileId);
        return favoriteFoodRepository.existsByDiaryProfileIdAndItemTypeAndItemId(
                diaryProfileId, itemType, itemId
        );
    }

    @Transactional(readOnly = true)
    public List<Long> listItemIds(Long diaryProfileId, ItemType itemType) {
        ensureOwner(diaryProfileId);
        return favoriteFoodRepository
                .findAllByDiaryProfileIdAndItemType(diaryProfileId, itemType)
                .stream()
                .map(FavoriteFoodEntity::getItemId)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> search(Long diaryProfileId, String query) {
        ensureOwner(diaryProfileId);
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        List<Map<String, Object>> result = new ArrayList<>();

        List<Long> productIds = listItemIds(diaryProfileId, ItemType.PRODUCT);
        if (!productIds.isEmpty()) {
            for (ProductEntity p : productRepository.findAllById(productIds)) {
                if (p.getDeletedAt() != null) {
                    continue;
                }
                String code = p.getCode() == null ? "" : p.getCode().toLowerCase(Locale.ROOT);
                String desc = p.getDescription() == null ? "" : p.getDescription().toLowerCase(Locale.ROOT);
                if (q.isEmpty() || code.contains(q) || desc.contains(q)) {
                    result.add(Map.of(
                            "itemType", ItemType.PRODUCT.name(),
                            "itemId", p.getId(),
                            "name", p.getCode() == null ? "" : p.getCode(),
                            "description", p.getDescription() == null ? "" : p.getDescription(),
                            "photoPath", p.getPhotoPath() == null ? "" : p.getPhotoPath()
                    ));
                }
            }
        }

        List<Long> recipeIds = listItemIds(diaryProfileId, ItemType.RECIPE);
        if (!recipeIds.isEmpty()) {
            for (RecipeEntity r : recipeRepository.findAllById(recipeIds)) {
                if (r.getDeletedAt() != null) {
                    continue;
                }
                String name = r.getName() == null ? "" : r.getName().toLowerCase(Locale.ROOT);
                String desc = r.getDescription() == null ? "" : r.getDescription().toLowerCase(Locale.ROOT);
                if (q.isEmpty() || name.contains(q) || desc.contains(q)) {
                    result.add(Map.of(
                            "itemType", ItemType.RECIPE.name(),
                            "itemId", r.getId(),
                            "name", r.getName() == null ? "" : r.getName(),
                            "description", r.getDescription() == null ? "" : r.getDescription(),
                            "photoPath", r.getPhotoPath() == null ? "" : r.getPhotoPath()
                    ));
                }
            }
        }

        return result;
    }

    private void ensureOwner(Long diaryProfileId) {
        var ownerId = diaryProfileApi.getOwnerUserIdInternal(diaryProfileId);
        foodPolicy.ensureIsOwner(currentUser, ownerId);
    }

    private void ensureItemExists(ItemType itemType, Long itemId) {
        boolean exists = switch (itemType) {
            case PRODUCT -> productRepository.existsById(itemId);
            case RECIPE -> recipeRepository.existsById(itemId);
        };
        if (!exists) {
            throw new EntityNotFoundException(itemType.name().toLowerCase() + " not found");
        }
    }
}
