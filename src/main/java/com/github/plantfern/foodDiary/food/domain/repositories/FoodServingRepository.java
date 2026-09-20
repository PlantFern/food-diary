package com.github.plantfern.foodDiary.food.domain.repositories;


import com.github.plantfern.foodDiary.food.api.ItemType;
import com.github.plantfern.foodDiary.food.domain.entities.FoodServingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FoodServingRepository extends JpaRepository<FoodServingEntity, Long> {

    List<FoodServingEntity> findAllByItemIdAndItemType
            (Long itemId, ItemType itemType);
}
