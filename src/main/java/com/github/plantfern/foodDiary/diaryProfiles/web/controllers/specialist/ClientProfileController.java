package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.specialist;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.DiaryProfileDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.DiaryProfileMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.DiaryProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/api/specialist/diary-profile")
public class ClientProfileController {

    private final DiaryProfileService diaryProfileService;
    private final DiaryProfileMapper diaryProfileMapper;

    @Autowired
    public ClientProfileController(
            DiaryProfileService diaryProfileService,
            DiaryProfileMapper diaryProfileMapper){
        this.diaryProfileService = diaryProfileService;
        this.diaryProfileMapper = diaryProfileMapper;
    }


    @GetMapping("/{id}")
    public ResponseEntity<DiaryProfileDto> findDiary(@PathVariable Long id){
        var response = diaryProfileService.findById( id );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<DiaryProfileDto>> findDiaries(@RequestBody Collection<Long> ids){
        var response = diaryProfileService.findAllById(ids);

        return ResponseEntity.ok(response.stream().map(diaryProfileMapper::toDto).toList());
    }
}
