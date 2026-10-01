package com.github.plantfern.foodDiary.specialists.web.controllers.client;


import com.github.plantfern.foodDiary.specialists.api.RelationType;
import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.services.UserRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@RestController
@RequestMapping("/api/diary-profile/{diaryProfileId}/specialists")
public class ClientAccessController {

    private final UserRelationService userRelationService;

    public ClientAccessController(
            UserRelationService userRelationService
    ) {
        this.userRelationService = userRelationService;
    }

    @GetMapping
    public ResponseEntity<List<UserRelationDto>> getAllRelations(@PathVariable Long diaryProfileId) {

        return ResponseEntity.ok(userRelationService.getByDiaryProfileId(diaryProfileId));
    }

    @PutMapping("/spacialist/{specialistId}/initiate")
    public ResponseEntity<Void> initiate(
            @PathVariable Long diaryProfileId,
            @PathVariable Long specialistId,
            @RequestParam RelationType relationType
    ){

        userRelationService.create(diaryProfileId, specialistId, relationType);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/end/{userRelationId}")
    public ResponseEntity<Void> end(@PathVariable Long userRelationId){

        userRelationService.deactivate(userRelationId);
        return ResponseEntity.ok().build();
    }
}
