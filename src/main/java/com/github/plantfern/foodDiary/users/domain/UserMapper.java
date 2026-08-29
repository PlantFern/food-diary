package com.github.plantfern.foodDiary.users.domain;

import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.api.UserDto;
import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto toDto(UserEntity user){
        Set<RoleName> roles = user
                .getUserRoles()
                .stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());

        return new UserDto(
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.isEnabled(),
                roles
        );
    }
}
