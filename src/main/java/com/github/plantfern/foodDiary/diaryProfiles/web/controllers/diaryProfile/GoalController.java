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

        List<GoalNutrientDto> goalNutrientDtoList = new ArrayList<>();
        for(var goalNutrient : request.nutrientGoals()){
            goalNutrientDtoList.add(new GoalNutrientDto(
                    0L,
                    goalNutrient.nutrientId(),
                    goalNutrient.amount()
            ));
        }

        return ResponseEntity.ok(goalService.create(
                diaryProfileId,
                request.plannedWeight(),
                request.startDate(),
                request.plannedEndDate(),
                goalNutrientDtoList
                )
        );
    }

    @PutMapping ("/{diaryProfileId}")
    public ResponseEntity<GoalDto> update(
            @PathVariable Long diaryProfileId,
            @RequestBody GoalRequest request
    ) {

        List<GoalNutrientDto> goalNutrientDtoList = new ArrayList<>();
        for(var goalNutrient : request.nutrientGoals()){
            goalNutrientDtoList.add(new GoalNutrientDto(
                    0L,
                    goalNutrient.nutrientId(),
                    goalNutrient.amount()
            ));
        }

        return ResponseEntity.ok(goalService.create(
                        diaryProfileId,
                        request.plannedWeight(),
                        request.startDate(),
                        request.plannedEndDate(),
                        goalNutrientDtoList
                )
        );
    }

    @DeleteMapping("/{diaryProfileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long diaryProfileId
    ) {

        goalService.delete(diaryProfileId);
    }

    @GetMapping("/{diaryProfileId}")
    public ResponseEntity<List<GoalDto>> getAll(
            @PathVariable Long diaryProfileId
    ) {

        return  ResponseEntity.ok(goalService.getAllByDiaryProfile(diaryProfileId));
    }

    @GetMapping("/latest/{diaryProfileId}")
    public ResponseEntity<GoalDto> getLatest(
            @PathVariable Long diaryProfileId
    ) {

        return  ResponseEntity.ok(goalService.getLatestByDiaryProfile(diaryProfileId));
    }

    @GetMapping("/active/{diaryProfileId}")
    public ResponseEntity<GoalDto> getActive(
            @PathVariable Long diaryProfileId
    ) {

        return  ResponseEntity.ok(goalService.getActiveByDiaryProfile(diaryProfileId));
    }

    @GetMapping("/get-one/{goalId}")
    public ResponseEntity<GoalDto> getById(
            @PathVariable Long goalId
    ) {

        return ResponseEntity.ok(goalService.getById(goalId));
    }
}
