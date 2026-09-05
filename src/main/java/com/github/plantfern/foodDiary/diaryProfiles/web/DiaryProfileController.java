package com.github.plantfern.foodDiary.diaryProfiles.web;


import com.github.plantfern.foodDiary.diaryProfiles.domain.DiaryProfileMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.DiaryProfileService;
import com.github.plantfern.foodDiary.diaryProfiles.domain.dto.DiaryProfileDto;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.ProfileDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/diaryProfile")
public class DiaryProfileController {

    private final DiaryProfileService diaryProfileService;

    @Autowired
    public DiaryProfileController(
            DiaryProfileService diaryProfileService,
            DiaryProfileMapper diaryProfileMapper){
        this.diaryProfileService = diaryProfileService;
    }


    @PutMapping("/")
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

    @GetMapping("/{id}")
    public ResponseEntity<DiaryProfileDto> findDiary(@PathVariable Long id){
        var response = diaryProfileService.findById( id );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<DiaryProfileDto>> findDiaries(@RequestBody Collection<Long> ids){
        var response = diaryProfileService.findAllById(ids);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<DiaryProfileDto> updateDiary(
            @PathVariable Long id,
            @RequestParam  ProfileDataRequest profileDataRequest){
        diaryProfileService.update(
                id,
                profileDataRequest.height(),
                profileDataRequest.birthDate(),
                profileDataRequest.genderCode()
        );

        return ResponseEntity.ok().build();
    }
}
