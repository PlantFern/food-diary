package com.github.plantfern.foodDiary.users.web.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;


public record RegisterRequest(
        @NotNull String email,
        String login,
        @NotNull String password
) {
}
