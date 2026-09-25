package com.github.plantfern.foodDiary.users.api;

import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public interface UserApi {

    boolean existsByIdInternal(Long id);

    UserDto getByIdInternal(Long id);
    UserDto getByEmailInternal(String email);

    void assignRolesInternal(Long userId, Set<RoleName> roles);
} // UserApi
