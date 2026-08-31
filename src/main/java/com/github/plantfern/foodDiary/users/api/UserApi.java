package com.github.plantfern.foodDiary.users.api;

import java.util.Optional;
import java.util.Set;

public interface UserApi {

    Optional<UserDto> findById(Long id);
    Optional<UserDto> findByEmail(String email);

    boolean existsById(Long userId);

    UserDto register(String email, String password);

    void assignRoles(Long userId, Set<RoleName> roles);

    boolean hasRole(Long userId, RoleName role);
} // UserApi
