package com.github.plantfern.foodDiary.food.domain.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter

@Entity
@Table( name = "entity_statuses" )
public class EntityStatusEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 40)
    private String code;


    protected EntityStatusEntity() {}

    public EntityStatusEntity(String code) {
        this.code = code;
    }
}