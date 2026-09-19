package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.onboarding;


import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.DiaryProfileMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.DiaryProfileService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.ProfileDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/diary-profile/onboarding")
public class OnboardingController {

    private final DiaryProfileService diaryProfileService;

    @Autowired
    public OnboardingController(
            DiaryProfileService diaryProfileService,
            DiaryProfileMapper diaryProfileMapper){
        this.diaryProfileService = diaryProfileService;
    }


    @PostMapping("")
    public ResponseEntity<Void> createDiary(
            @RequestParam ProfileDataRequest profileDataRequest
    ){
        diaryProfileService.create(
                profileDataRequest.height(),
                profileDataRequest.birthDate(),
                profileDataRequest.genderCode()
        );

        return ResponseEntity.ok().build();
    }
}
