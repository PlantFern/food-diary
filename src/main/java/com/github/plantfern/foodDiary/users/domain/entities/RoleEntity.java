package com.github.plantfern.foodDiary.users.domain.entities;


import com.github.plantfern.foodDiary.users.api.RoleName;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@Entity
@Table(name = "roles")
public class RoleEntity {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true, length = 32)
    private RoleName name;

    protected RoleEntity() {}

    public RoleEntity(RoleName name){
        this.name = name;
    }
}
