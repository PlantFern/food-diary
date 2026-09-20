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

    @PutMapping("/weight-logs/{weightLogId}")
    public ResponseEntity<WeightLogDto> update(
            @PathVariable Long weightLogId,
            @RequestParam Float weight
    ) {

        return ResponseEntity.ok(weightLogService
                .update(
                        weightLogId,
                        weight
                )
        );
    }

    @DeleteMapping("/weight-logs/{weightLogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long weightLogId
    ) {

        weightLogService.delete(weightLogId);
    }

    @GetMapping("/diary-profile/{diaryProfileId}/get-all")
    public ResponseEntity<List<WeightLogDto>> getByDiaryProfile(
            @PathVariable Long diaryProfileId
    ) {

        return ResponseEntity.ok(weightLogService
                .getByDiaryProfileId(diaryProfileId));
    }

    @GetMapping("/diary-profile/{diaryProfileId}/get-latest")
    public ResponseEntity<WeightLogDto> getLatestByDiaryProfile(
            @PathVariable Long diaryProfileId
    ) {

        return ResponseEntity.ok(weightLogService
                .getLatestByDiaryProfileId(diaryProfileId)
        );
    }

    @GetMapping("/weight-logs/{weightLogId}")
    public ResponseEntity<WeightLogDto> getById(
            @PathVariable Long weightLogId
    ) {

        return ResponseEntity.ok(weightLogService.getById(weightLogId));
    }

    @GetMapping("/diary-profile/{diaryProfileId}/get-by-period")
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
