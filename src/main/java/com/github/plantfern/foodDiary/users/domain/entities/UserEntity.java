package com.github.plantfern.foodDiary.users.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Setter
@Getter

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(
                name = "unique_email_deleted_at",
                columnNames = {"email", "deleted_at"}
        )
    }
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String email;

    @Column(
            nullable = true,
            unique = true,
            length = 30
    )
    private String login;

    @Column(
            name = "hash_password",
            nullable = false,
            length = 60
    )
    private String hashPassword;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoleEntity> userRoles = new HashSet<>();


    protected UserEntity() {} // конструктор для Hibernate

    public UserEntity(String email, String hashPassword) {
        this.email = email;
        this.hashPassword = hashPassword;
    }

    public void addRole(RoleEntity role){
        boolean alreadyHas = userRoles.stream()
                .anyMatch(ur -> ur.getRole().getName().equals(role.getName()));
        if(!alreadyHas){
            userRoles.add(new UserRoleEntity(this, role));
        }
    }

    public void replaceRoles(Set<RoleEntity> roles){
        userRoles.clear();
        roles.forEach(this::addRole);
    }
}