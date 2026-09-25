package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.onboarding;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryProfileDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.DiaryProfileMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.DiaryProfileService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.ExtendedProfileDataRequest;
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
            @RequestBody ProfileDataRequest profileDataRequest
    ){
        diaryProfileService.create(
                profileDataRequest.height(),
                profileDataRequest.birthDate(),
                profileDataRequest.genderCode()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-and-calculate-goal")
    public ResponseEntity<DiaryProfileDto> createDiaryWithCalculatedGoal(
            @RequestBody ExtendedProfileDataRequest extendedProfileDataRequest
    ){
        return ResponseEntity.ok(diaryProfileService.createWithCalculatedGoal(
                extendedProfileDataRequest.height(),
                extendedProfileDataRequest.birthDate(),
                extendedProfileDataRequest.genderId(),
                extendedProfileDataRequest.weight(),
                extendedProfileDataRequest.activityLevel(),
                extendedProfileDataRequest.goalType()
        ));
    }
}
