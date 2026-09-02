package com.github.plantfern.foodDiary.users.domain.entities;

import com.github.plantfern.foodDiary.users.api.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


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
    private String hashPassword; // user_exemple_pass - пароль для всех аккаунтов


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoleEntity> userRoles = new HashSet<>();


    public boolean isEnabled(){
        return deletedAt == null;
    }


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

    public void addRoles(Set<RoleEntity> roles) {
        for (RoleEntity role : roles) {
            addRole(role); // используем существующий метод
        }
    }

    public void replaceRoles(Set<RoleEntity> roles){
        userRoles.clear();
        roles.forEach(this::addRole);
    }

    public Set<RoleName> roleNames() {
        return userRoles.stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());
    }


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        if (deletedAt == null) {
            deletedAt = LocalDateTime.now();
        }
    }

    public void restore() {
        deletedAt = null;
    }


    public boolean is(Long userId){
        return this.id != null && this.id.equals(userId);
    }

    public void ensureIs(Long userId) {
        if (!is(userId)) {
            throw new AccessDeniedException("Access denied to user" + this.id);
        }
    }
}