package com.github.plantfern.foodDiary.specialists.web.controllers;

import org.springframework.validation.annotation.Validated;

import com.github.plantfern.foodDiary.specialists.api.dto.SpecialistDto;
import com.github.plantfern.foodDiary.specialists.api.dto.SpecialistSearchItemDto;
import com.github.plantfern.foodDiary.specialists.domain.services.SpecialistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Validated
@RestController
@RequestMapping("/api/specialists")
public class PublicSpecialistController {

    private final SpecialistService specialistService;

    public PublicSpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<SpecialistDto> getSpecialist(@PathVariable Long id){
        return ResponseEntity.ok(specialistService.getById(id));
    }

    @GetMapping("")
    public ResponseEntity<?> getAllOrSearch(
            @RequestParam(required = false) String query
    ){
        if (query != null && !query.isBlank()) {
            return ResponseEntity.ok(specialistService.search(query.trim()));
        }
        return ResponseEntity.ok(specialistService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<SpecialistSearchItemDto>> search(
            @RequestParam(required = false, defaultValue = "") String query
    ) {
        return ResponseEntity.ok(specialistService.search(query == null ? "" : query.trim()));
    }
}
