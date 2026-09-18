package com.github.plantfern.foodDiary.specialists.web.controllers.specialist;


import com.github.plantfern.foodDiary.specialists.domain.services.SpecialistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/specialist/specialists")
@PreAuthorize("hasAnyRole('SPECIALIST')")
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(
            SpecialistService specialistService
    ) {
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
}
