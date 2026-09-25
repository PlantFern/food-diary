package com.github.plantfern.foodDiary.users.web.controllers;

import com.github.plantfern.foodDiary.users.api.UserDto;
import com.github.plantfern.foodDiary.users.domain.UserService;
import jakarta.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUserAccount(@ModelAttribute String email,
                                                     @ModelAttribute String password,
                                                     @ModelAttribute @Null String login
    ){

        return ResponseEntity.ok(
                userService.register(
                        email,
                        password,
                        login)
        );
    }
}
