package com.github.plantfern.foodDiary.users.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(
        name="user_roles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
)
public class UserRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id")
    private RoleEntity role;


    // contractors
    protected UserRoleEntity() {}

    UserRoleEntity(UserEntity user, RoleEntity role) {
        this.user = user;
        this.role = role;
    }
} // UserRoleEntity
