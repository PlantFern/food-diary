package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.domain.entities.FavoriteFoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FavoriteFoodRepository extends JpaRepository<FavoriteFoodEntity, Long> {

    boolean existsByDiaryProfileIdAndItemTypeAndItemId(
            Long diaryProfileId, ItemType itemType, Long itemId);
}
