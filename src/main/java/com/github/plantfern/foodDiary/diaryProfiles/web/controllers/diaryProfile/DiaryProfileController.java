package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryProfileDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.DiaryProfileService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.ProfileDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diary-profile")
public class DiaryProfileController {

    private final DiaryProfileService diaryProfileService;

    @Autowired
    public DiaryProfileController(DiaryProfileService diaryProfileService) {
        this.diaryProfileService = diaryProfileService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<DiaryProfileDto> getMyProfile() {
        return ResponseEntity.ok(diaryProfileService.getByCurrentUser());
    }

    @PutMapping("/{diaryProfileId}")
    public ResponseEntity<DiaryProfileDto> updateDiary(
            @PathVariable Long diaryProfileId,
            @RequestBody ProfileDataRequest profileDataRequest
    ) {
        return ResponseEntity.ok(diaryProfileService.update(
                diaryProfileId,
                profileDataRequest.height(),
                profileDataRequest.birthDate(),
                profileDataRequest.genderId()
        ));
    }
}
