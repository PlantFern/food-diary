package com.github.plantfern.foodDiary.food.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@Entity
@Table(name = "data_sources")
public class DataSourceEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 45, unique = true, nullable = false)
    private String code;


    protected DataSourceEntity() {}

    public DataSourceEntity(String code) {
        this.code = code;
    }
}
