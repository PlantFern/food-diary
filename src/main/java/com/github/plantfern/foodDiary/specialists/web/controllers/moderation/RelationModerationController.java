package com.github.plantfern.foodDiary.specialists.web.controllers.moderation;


import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.mappers.UserRelationMapper;
import com.github.plantfern.foodDiary.specialists.domain.services.UserRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/moderation/specialists")
@PreAuthorize("hasAnyRole('SPECIALIST')")
public class RelationModerationController {

    private final UserRelationService userRelationService;
    private final UserRelationMapper userRelationMapper;

    public RelationModerationController(
            UserRelationService userRelationService,
            UserRelationMapper userRelationMapper)
    {
        this.userRelationService = userRelationService;
        this.userRelationMapper = userRelationMapper;
    }



    @GetMapping("/getAll")
    public ResponseEntity<List<UserRelationDto>> getUserRelations(){

        return ResponseEntity.ok(userRelationService.findAll());
    }

    @GetMapping("/by-diary-profile/{diaryProfileId}")
    public ResponseEntity<List<UserRelationDto>> getAllByDiaryProfile(@PathVariable Long diaryProfileId) {

        return ResponseEntity.ok(userRelationService.findBySpecialistId(diaryProfileId));
    }

    @GetMapping("/by-specialist/{specialistId}")
    public ResponseEntity<List<UserRelationDto>> getAllBySpecialist(@PathVariable Long specialistId) {

        return ResponseEntity.ok(userRelationService.findBySpecialistId(specialistId));
    }
}
