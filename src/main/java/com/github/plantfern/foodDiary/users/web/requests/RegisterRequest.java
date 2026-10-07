package com.github.plantfern.foodDiary.users.web.requests;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record RegisterRequest(
        @NotNull @Email @Size(max = 255) String email,
        @Size(min = 3, max = 30)  String login,
        @NotNull @Size(min = 8, max = 72) String password
) {
}
