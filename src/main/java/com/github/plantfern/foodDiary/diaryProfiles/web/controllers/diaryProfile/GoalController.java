package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalNutrientDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.GoalService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.GoalRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/diary-profile/goal")
public class GoalController {


    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }


    @PostMapping ("/{diaryProfileId}")
    public ResponseEntity<GoalDto> create(
            @PathVariable Long diaryProfileId,
            @RequestBody GoalRequest request
    ) {

        return ResponseEntity.ok(goalService.create(
                diaryProfileId,
                request.plannedWeight(),
                request.startDate(),
                request.plannedEndDate(),
                request.nutrientGoals()
                )
        );
    }

    @PutMapping ("/goals/{goalId}")
    public ResponseEntity<GoalDto> update(
            @PathVariable Long goalId,
            @RequestBody GoalRequest request
    ) {

        return ResponseEntity.ok(goalService.update(
                        goalId,
                        request.plannedWeight(),
                        request.startDate(),
                        request.plannedEndDate(),
                        request.nutrientGoals()
                )
        );
    }

    @DeleteMapping("/goals/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long goalId
    ) {

        goalService.delete(goalId);
    }

    @GetMapping("/diary-profile/{diaryProfileId}/get-all")
    public ResponseEntity<List<GoalDto>> getAll(
            @PathVariable Long diaryProfileId
    ) {

        return  ResponseEntity.ok(goalService.getAllByDiaryProfile(diaryProfileId));
    }

    @GetMapping("/diary-profile/{diaryProfileId}/latest")
    public ResponseEntity<GoalDto> getLatest(
            @PathVariable Long diaryProfileId
    ) {

        return  ResponseEntity.ok(goalService.getLatestByDiaryProfile(diaryProfileId));
    }

    @GetMapping("/diary-profile/{diaryProfileId}/active")
    public ResponseEntity<GoalDto> getActive(
            @PathVariable Long diaryProfileId
    ) {

        return  ResponseEntity.ok(goalService.getActiveByDiaryProfile(diaryProfileId));
    }

    @GetMapping("/goals/{goalId}")
    public ResponseEntity<GoalDto> getById(
            @PathVariable Long goalId
    ) {

        return ResponseEntity.ok(goalService.getById(goalId));
    }
}
