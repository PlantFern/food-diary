package com.github.plantfern.foodDiary.specialists.web.controllers.specialist;

import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.mappers.UserRelationMapper;
import com.github.plantfern.foodDiary.specialists.domain.services.UserRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/specialist/{specialistId}/relations")
public class SpecialistRelationsController {

    private final UserRelationService userRelationService;
    private final UserRelationMapper userRelationMapper;

    public SpecialistRelationsController(
            UserRelationService userRelationService,
            UserRelationMapper userRelationMapper
    ) {
        this.userRelationService = userRelationService;
        this.userRelationMapper = userRelationMapper;
    }


    @GetMapping("/{userRelationId}")
    public ResponseEntity<UserRelationDto> getUserRelation(@PathVariable Long userRelationId){

        return ResponseEntity.ok(userRelationMapper.toDto( userRelationService.getById(userRelationId)));
    }

    @GetMapping("")
    public ResponseEntity<List<UserRelationDto>> getAll(@PathVariable Long specialistId) {

        return ResponseEntity.ok(userRelationService.getBySpecialistId(specialistId));
    }

    @PostMapping("/{userRelationId}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long userRelationId){
        userRelationService.activate(userRelationId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userRelationId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long userRelationId){
        userRelationService.cancel(userRelationId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userRelationId}/end")
    public ResponseEntity<Void> end(@PathVariable Long userRelationId){
        userRelationService.deactivate(userRelationId);

        return ResponseEntity.ok().build();
    }
}
