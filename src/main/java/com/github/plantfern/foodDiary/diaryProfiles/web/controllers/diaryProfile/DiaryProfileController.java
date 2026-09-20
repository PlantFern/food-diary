package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryProfileDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.DiaryProfileMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.DiaryProfileService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.ProfileDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diary-profile/diary-profile")
public class DiaryProfileController {


    private final DiaryProfileService diaryProfileService;

    @Autowired
    public DiaryProfileController(
            DiaryProfileService diaryProfileService,
            DiaryProfileMapper diaryProfileMapper){
        this.diaryProfileService = diaryProfileService;
    }


    @GetMapping("/{diaryProfileId}")
    public ResponseEntity<DiaryProfileDto> findDiary(@PathVariable Long diaryProfileId){
        var response = diaryProfileService.getByIdWithPolicy( diaryProfileId );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{diaryProfileId}")
    public ResponseEntity<DiaryProfileDto> updateDiary(
            @PathVariable Long diaryProfileId,
            @RequestParam ProfileDataRequest profileDataRequest){
        diaryProfileService.update(
                diaryProfileId,
                profileDataRequest.height(),
                profileDataRequest.birthDate(),
                profileDataRequest.genderCode()
        );

        return ResponseEntity.ok().build();
    }
}
