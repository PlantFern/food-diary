package com.github.plantfern.foodDiary.users.domain.entities;


import com.github.plantfern.foodDiary.users.api.RoleName;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "roles")
public class RoleEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true, length = 32)
    private RoleName name;

    protected RoleEntity() {}

    RoleEntity(RoleName name){
        this.name = name;
    }
}
