package com.github.plantfern.foodDiary.specialists.web.controllers;

import org.springframework.validation.annotation.Validated;


import com.github.plantfern.foodDiary.specialists.domain.services.SpecialistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor

@Validated
@RestController
@RequestMapping("/api/specialists")
public class SpecialistOnboardingController {

    private final SpecialistService specialistService;


    @PostMapping("/")
    public ResponseEntity<Void> createSpecialist(){
        specialistService.create();

        return ResponseEntity.ok().build();
    }
}
