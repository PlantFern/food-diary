package com.github.plantfern.foodDiary.food.domain.views;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;


@Getter
@Entity
@Immutable
@Table(name = "v_products_basic")
public class VProductBasicEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "created_by_id")
    private Long createdById;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_code")
    private String categoryCode;

    @Column(name = "entity_status_id")
    private Long entityStatusId;

    @Column(name = "entity_status_code")
    private String entityStatusCode;

    @Column(name = "data_source_id")
    private Long dataSourceId;

    @Column(name = "data_source_code")
    private String dataSourceCode;

    @Column(name = "base_serving_id")
    private Long baseServingId;

    @Column(name = "serving_amount")
    private Long servingAmount;

    @Column(name = "gram_weight_per_serving")
    private Float gramWeightPerServing;

    @Column(name = "serving_unit_code")
    private String servingUnitCode;

    protected VProductBasicEntity() {}
}
