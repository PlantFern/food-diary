package com.github.plantfern.foodDiary.specialists.web.controllers.specialist;


import com.github.plantfern.foodDiary.specialists.domain.services.SpecialistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/specialist/{specialistId}")
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(
            SpecialistService specialistService
    ) {
        this.specialistService = specialistService;
    }

    @PostMapping("/changeState")
    public ResponseEntity<Void> changeActivity(){
        specialistService.updateActivity();

        return ResponseEntity.ok().build();
    }
}
