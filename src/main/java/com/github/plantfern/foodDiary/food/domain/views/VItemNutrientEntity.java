package com.github.plantfern.foodDiary.food.domain.views;


import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.util.Objects;


@Getter
@Entity
@Immutable
@Table(name = "v_item_nutrients")
@IdClass(VItemNutrientEntity.Pk.class)
public class VItemNutrientEntity {

    @Id
    @Column(name = "itemId")
    private Long itemId;

    @Id
    @Column(name = "itemType")
    private String itemType;

    @Id
    @Column(name = "nutrientId")
    private Long nutrientId;

    @Column(name = "nutrientCode")
    private String nutrientCode;

    @Column(name = "amountPer100g")
    private Float amountPer100g;

    @Column(name = "unitCode")
    private String unitCode;

    protected VItemNutrientEntity() {}

    public static class Pk implements Serializable {
        private Long itemId;
        private String itemType;
        private Long nutrientId;

        public Pk() {}

        public Pk(Long itemId, String itemType, Long nutrientId) {
            this.itemId = itemId;
            this.itemType = itemType;
            this.nutrientId = nutrientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pk pk)) return false;
            return Objects.equals(itemId, pk.itemId)
                    && Objects.equals(itemType, pk.itemType)
                    && Objects.equals(nutrientId, pk.nutrientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemId, itemType, nutrientId);
        }
    }
}
