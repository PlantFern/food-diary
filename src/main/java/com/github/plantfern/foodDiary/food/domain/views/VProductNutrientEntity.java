package com.github.plantfern.foodDiary.food.domain.views;


import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.util.Objects;


@Getter
@Immutable
@Entity
@Table(name = "v_product_nutrients")
@IdClass(VProductNutrientEntity.Pk.class)
public class VProductNutrientEntity {

    @Id
    @Column(name = "productId")
    private Long productId;

    @Id
    @Column(name = "nutrientId")
    private Long nutrientId;

    @Column(name = "productCode")
    private String productCode;

    @Column(name = "nutrientCode")
    private String nutrientCode;

    @Column(name = "amountPer100g")
    private Float amountPer100g;

    @Column(name = "unitCode")
    private String unitCode;

    protected VProductNutrientEntity() {}

    public static class Pk implements Serializable {
        private Long productId;
        private Long nutrientId;

        public Pk() {}

        public Pk(Long productId, Long nutrientId) {
            this.productId = productId;
            this.nutrientId = nutrientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pk pk)) return false;
            return Objects.equals(productId, pk.productId)
                    && Objects.equals(nutrientId, pk.nutrientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, nutrientId);
        }
    }
}
