package com.github.plantfern.foodDiary.specialists.web.controllers.specialist;


import com.github.plantfern.foodDiary.specialists.api.dto.SpecialistDto;
import com.github.plantfern.foodDiary.specialists.domain.services.SpecialistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/specialist")
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(
            SpecialistService specialistService
    ) {
        this.specialistService = specialistService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<SpecialistDto> getMyProfile() {
        return ResponseEntity.ok(specialistService.getByCurrentUser());
    }

    @PostMapping("/changeState")
    public ResponseEntity<Void> changeActivity(){
        specialistService.updateActivity();

        return ResponseEntity.ok().build();
    }
}
