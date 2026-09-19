package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryProfileDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.ProfileFeatureSettingsDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.DiaryProfileMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.DiaryProfileService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.ProfileDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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


    @GetMapping("/{id}")
    public ResponseEntity<DiaryProfileDto> findDiary(@PathVariable Long id){
        var response = diaryProfileService.findById( id );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<DiaryProfileDto> updateDiary(
            @PathVariable Long id,
            @RequestParam ProfileDataRequest profileDataRequest){
        diaryProfileService.update(
                id,
                profileDataRequest.height(),
                profileDataRequest.birthDate(),
                profileDataRequest.genderCode()
        );

        return ResponseEntity.ok().build();
    }
}
