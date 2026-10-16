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
    @Column(name = "productId")
    private Long productId;

    @Column(name = "productCode")
    private String productCode;

    @Column(name = "productDescription")
    private String productDescription;

    @Column(name = "photoPath")
    private String photoPath;

    @Column(name = "isPublic")
    private Boolean isPublic;

    @Column(name = "createdById")
    private Long createdById;

    @Column(name = "categoryId")
    private Long categoryId;

    @Column(name = "categoryCode")
    private String categoryCode;

    @Column(name = "entityStatusId")
    private Long entityStatusId;

    @Column(name = "entityStatusCode")
    private String entityStatusCode;

    @Column(name = "dataSourceId")
    private Long dataSourceId;

    @Column(name = "dataSourceCode")
    private String dataSourceCode;

    @Column(name = "baseServingId")
    private Long baseServingId;

    @Column(name = "servingAmount")
    private Long servingAmount;

    @Column(name = "gramWeightPerServing")
    private Float gramWeightPerServing;

    @Column(name = "servingUnitCode")
    private String servingUnitCode;

    protected VProductBasicEntity() {}
}
