package com.github.plantfern.foodDiary.specialists.web.controllers.client;


import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.services.UserRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/diary-profile/{diaryProfileId}/specialists")
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

    @PutMapping("/initiate/{userRelationId}")
    public ResponseEntity<Void> initiate(@PathVariable Long userRelationId){

        userRelationService.activate(userRelationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/end/{userRelationId}")
    public ResponseEntity<Void> end(@PathVariable Long userRelationId){

        userRelationService.deactivate(userRelationId);
        return ResponseEntity.ok().build();
    }
}
