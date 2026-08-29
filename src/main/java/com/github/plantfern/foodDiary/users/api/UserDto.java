package com.github.plantfern.foodDiary.users.api;

import java.util.Set;

public record UserDto (
        Long id,
        String login,
        String email,
        boolean enabled,
        Set<RoleName> roles)
{ }
