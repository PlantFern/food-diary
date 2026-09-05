package com.github.plantfern.foodDiary.specialists.web;

import com.github.plantfern.foodDiary.specialists.api.SpecialistDto;
import com.github.plantfern.foodDiary.specialists.domain.SpecialistService;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/specialist")
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }

    @PutMapping("/")
    public ResponseEntity<Void> createSpecialist(){
        specialistService.create();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeState")
    public ResponseEntity<Void> changeActivity(){
        specialistService.updateActivity();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialistDto> getSpecialist(@PathVariable Long id){
        return ResponseEntity.ok(specialistService.findById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<SpecialistDto>> getAllSpecialists(){
        return ResponseEntity.ok(specialistService.findAll());
    }
}
