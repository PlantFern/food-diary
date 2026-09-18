package com.github.plantfern.foodDiary.users.web.controllers;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController {

    @PostMapping("/registration")
    public void createUserAccount(@ModelAttribute String email,
                                  @ModelAttribute String password){

    }
}
