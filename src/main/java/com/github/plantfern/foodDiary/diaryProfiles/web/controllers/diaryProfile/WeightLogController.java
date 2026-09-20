package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.WeightLogService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.DatePeriodRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/diary-profile/weight-log")
public class WeightLogController {


    private final WeightLogService weightLogService;

    public WeightLogController(WeightLogService weightLogService) {
        this.weightLogService = weightLogService;
    }

    @PostMapping("/{diaryProfileId}")
    public ResponseEntity<WeightLogDto> create(
            @PathVariable Long diaryProfileId,
            @RequestParam Float weight
    ) {

        return ResponseEntity.ok(weightLogService
                .create(
                        diaryProfileId,
                        weight
                )
        );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<WeightLogDto> update(
            @PathVariable Long commentId,
            @RequestParam Float weight
    ) {

        return ResponseEntity.ok(weightLogService
                .update(
                        commentId,
                        weight
                )
        );
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long commentId
    ) {

        weightLogService.delete(commentId);
    }

    @GetMapping("/get-all/{diaryProfileId}")
    public ResponseEntity<List<WeightLogDto>> getByDiaryProfile(
            @PathVariable Long diaryProfileId
    ) {

        return ResponseEntity.ok(weightLogService
                .getByDiaryProfileId(diaryProfileId));
    }

    @GetMapping("/get-latest/{diaryProfileId}")
    public ResponseEntity<WeightLogDto> getLatestByDiaryProfile(
            @PathVariable Long diaryProfileId
    ) {

        return ResponseEntity.ok(weightLogService
                .getLatestByDiaryProfileId(diaryProfileId)
        );
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<WeightLogDto> getById(
            @PathVariable Long commentId
    ) {

        return ResponseEntity.ok(weightLogService.getById(commentId));
    }

    @GetMapping("/get-by-petiod/{diaryProfileId}")
    public ResponseEntity<List<WeightLogDto>> getByPeriod(
            @PathVariable Long diaryProfileId,
            @ModelAttribute DatePeriodRequest request
            ){

        return ResponseEntity.ok(weightLogService.getByDiaryProfileAndPeriod(
                diaryProfileId,
                request.startPeriod(),
                request.endPeriod()
        ));
    }
}
