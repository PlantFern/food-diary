package com.github.plantfern.foodDiary.users.web.requests;

import com.github.plantfern.foodDiary.users.api.RoleName;
import jakarta.validation.constraints.NotBlank;


public record RoleRequest(@NotBlank RoleName roleName) {
}
