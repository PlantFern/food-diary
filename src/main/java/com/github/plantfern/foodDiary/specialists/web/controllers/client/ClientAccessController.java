package com.github.plantfern.foodDiary.specialists.web.controllers.client;


import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.mappers.UserRelationMapper;
import com.github.plantfern.foodDiary.specialists.domain.services.UserRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/client/specialists")
public class ClientAccessController {

    private final UserRelationService userRelationService;
    private final UserRelationMapper userRelationMapper;

    public ClientAccessController(
            UserRelationService userRelationService,
            UserRelationMapper userRelationMapper
    ) {
        this.userRelationService = userRelationService;
        this.userRelationMapper = userRelationMapper;
    }

    @GetMapping("/{diaryProfileId}")
    public ResponseEntity<List<UserRelationDto>> getAllRelations(@PathVariable Long diaryProfileId) {

        return ResponseEntity.ok(userRelationService.findBySpecialistId(diaryProfileId));
    }

    @PutMapping("/initiate")
    public ResponseEntity<Void> activate(@RequestParam Long id){

        userRelationService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/end")
    public ResponseEntity<Void> end(@RequestParam Long id){

        userRelationService.deactivate(id);
        return ResponseEntity.ok().build();
    }
}
