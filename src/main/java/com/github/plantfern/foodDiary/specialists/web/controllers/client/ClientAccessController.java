package com.github.plantfern.foodDiary.specialists.web.controllers.client;


import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.services.UserRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/client/specialists")
public class ClientAccessController {

    private final UserRelationService userRelationService;

    public ClientAccessController(
            UserRelationService userRelationService
    ) {
        this.userRelationService = userRelationService;
    }

    @GetMapping("{diaryProfileId}")
    public ResponseEntity<List<UserRelationDto>> getAllRelations(@PathVariable Long diaryProfileId) {

        return ResponseEntity.ok(userRelationService.getByDiaryProfileId(diaryProfileId));
    }

    @PutMapping("/initiate")
    public ResponseEntity<Void> initiate(@RequestParam Long id){

        userRelationService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/end")
    public ResponseEntity<Void> end(@RequestParam Long id){

        userRelationService.deactivate(id);
        return ResponseEntity.ok().build();
    }
}
