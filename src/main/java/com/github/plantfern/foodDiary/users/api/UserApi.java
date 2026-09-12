package com.github.plantfern.foodDiary.users.api;

import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public interface UserApi {

    boolean existsByIdInternal(Long id);

    UserDto findByIdInternal(Long id);
    UserDto findByEmailInternal(String email);

    void assignRolesInternal(Long userId, Set<RoleName> roles);
} // UserApi
