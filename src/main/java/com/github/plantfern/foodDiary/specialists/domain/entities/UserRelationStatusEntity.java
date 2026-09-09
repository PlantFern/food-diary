package com.github.plantfern.foodDiary.specialists.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@Entity
@Table(
        name="user_relation_statuses",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_relation_statuses_code", columnNames = "code")
        }
)
public class UserRelationStatusEntity {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=20)
    private String code;


    protected UserRelationStatusEntity(){}

    public UserRelationStatusEntity(String code){
        this.code = code;
    }
}
