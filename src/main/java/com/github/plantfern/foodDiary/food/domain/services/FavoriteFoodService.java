package com.github.plantfern.foodDiary.food.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.apis.DiaryProfileApi;
import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.domain.entities.FavoriteFoodEntity;
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

import java.util.List;


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
            // already favorite
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
