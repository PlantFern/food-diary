package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.GoalDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/diary-profile/goal")
public class GoalController {


    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }


    @GetMapping("/{diaryProfileId}")
    public ResponseEntity<List<GoalDto>> getAll(
            @PathVariable Long diaryProfileId
    ) {

        return  ResponseEntity.ok(goalService.getAllByDiaryProfile(diaryProfileId));
    }
}
