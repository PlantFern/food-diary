package com.github.plantfern.foodDiary.food.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter

@Entity
@Table(name = "product_data_sources")
public class ProductDataSourceEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            insertable = false,
            updatable = false
    )
    private ProductEntity product;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "data_source_id",
            insertable = false,
            updatable = false
    )
    private DataSourceEntity dataSource;

    @Column(
            name = "data_source_id",
            nullable = false
    )
    private Long dataSourceId;

    @Column(name = "external_id", length = 45, nullable = false)
    private String externalId;


    protected ProductDataSourceEntity() {}

    public ProductDataSourceEntity(Long productId, Long dataSourceId, String externalId) {
        this.productId = productId;
        this.dataSourceId = dataSourceId;
        this.externalId = externalId;
    }
}