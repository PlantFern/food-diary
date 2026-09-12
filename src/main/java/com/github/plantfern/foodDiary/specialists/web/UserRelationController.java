package com.github.plantfern.foodDiary.specialists.web;

import com.github.plantfern.foodDiary.specialists.api.UserRelationDto;
import com.github.plantfern.foodDiary.specialists.domain.UserRelationMapper;
import com.github.plantfern.foodDiary.specialists.domain.UserRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/userRelation")
public class UserRelationController {

    private final UserRelationService userRelationService;
    private final UserRelationMapper userRelationMapper;

    public UserRelationController(UserRelationService userRelationService, UserRelationMapper userRelationMapper) {
        this.userRelationService = userRelationService;
        this.userRelationMapper = userRelationMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRelationDto> getUserRelation(@PathVariable Long id){
        return ResponseEntity.ok(userRelationMapper.toDto( userRelationService.findById(id)));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<UserRelationDto>> getUserRelations(){
        return ResponseEntity.ok(userRelationService.findAll());
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
