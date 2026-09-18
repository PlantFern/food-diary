package com.github.plantfern.foodDiary.specialists.web.controllers.specialist;

import com.github.plantfern.foodDiary.specialists.api.dto.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.mappers.UserRelationMapper;
import com.github.plantfern.foodDiary.specialists.domain.services.SpecialistService;
import com.github.plantfern.foodDiary.specialists.domain.services.UserRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/specialist/relations")
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


    @GetMapping("/{id}")
    public ResponseEntity<UserRelationDto> getUserRelation(@PathVariable Long id){

        return ResponseEntity.ok(userRelationMapper.toDto( userRelationService.findById(id)));
    }

    @GetMapping("/{specialistId}")
    public ResponseEntity<List<UserRelationDto>> getAll(@PathVariable Long specialistId) {

        return ResponseEntity.ok(userRelationService.findBySpecialistId(specialistId));
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activate(@RequestParam Long id){
        userRelationService.activate(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancel(@RequestParam Long id){
        userRelationService.cancel(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/end")
    public ResponseEntity<Void> end(@RequestParam Long id){
        userRelationService.deactivate(id);

        return ResponseEntity.ok().build();
    }
}
