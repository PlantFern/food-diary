package com.github.plantfern.foodDiary.users.api;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;


@Component
public interface UserApi {

    UserDto findById(Long id);
    UserDto findByEmail(String email);

    UserDto register(String email, String password);

    void assignRoles(Long userId, Set<RoleName> roles);
} // UserApi
