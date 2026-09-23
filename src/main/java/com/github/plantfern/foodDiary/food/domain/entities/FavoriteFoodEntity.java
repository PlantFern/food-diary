package com.github.plantfern.foodDiary.food.domain.entities;


import com.github.plantfern.foodDiary.food.api.ItemType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter

@Entity
@Table(name = "favorite_food")
public class FavoriteFoodEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(
            name = "diary_profile_id",
            nullable = false
    )
    private Long diaryProfileId;


    protected FavoriteFoodEntity() {}

    public FavoriteFoodEntity(ItemType itemType, Long itemId, Long diaryProfileId) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.diaryProfileId = diaryProfileId;
    }
}