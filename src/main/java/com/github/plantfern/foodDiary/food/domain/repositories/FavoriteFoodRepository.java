package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.domain.entities.FavoriteFoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FavoriteFoodRepository extends JpaRepository<FavoriteFoodEntity, Long> {

    boolean existsByDiaryProfileIdAndItemTypeAndItemId(
            Long diaryProfileId, ItemType itemType, Long itemId);

    Optional<FavoriteFoodEntity> findByDiaryProfileIdAndItemTypeAndItemId(
            Long diaryProfileId, ItemType itemType, Long itemId);

    void deleteByDiaryProfileIdAndItemTypeAndItemId(
            Long diaryProfileId, ItemType itemType, Long itemId);

    List<FavoriteFoodEntity> findAllByDiaryProfileIdAndItemType(
            Long diaryProfileId, ItemType itemType);
}
