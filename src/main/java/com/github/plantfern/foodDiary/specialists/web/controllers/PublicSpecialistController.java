package com.github.plantfern.foodDiary.specialists.web.controllers;

import com.github.plantfern.foodDiary.specialists.api.dto.SpecialistDto;
import com.github.plantfern.foodDiary.specialists.domain.services.SpecialistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/specialists")
public class PublicSpecialistController {

    private final SpecialistService specialistService;

    public PublicSpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<SpecialistDto> getSpecialist(@PathVariable Long id){
        return ResponseEntity.ok(specialistService.findById(id));
    }

    @GetMapping("")
    public ResponseEntity<List<SpecialistDto>> getAllSpecialists(){
        return ResponseEntity.ok(specialistService.findAll());
    }
}
