package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.specialist;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.SleepLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.SleepLogService;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.WeightLogService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.DatePeriodRequest;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.DateTimePeriodRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/specialist/log")
public class ClientLogController {


    private final SleepLogService sleepLogService;
    private final WeightLogService weightLogService;

    public ClientLogController(SleepLogService sleepLogService, WeightLogService weightLogService) {
        this.sleepLogService = sleepLogService;
        this.weightLogService = weightLogService;
    }

    @GetMapping("/diary-profile/{diaryProfile}/{selectedDay}")
    public ResponseEntity<List<SleepLogDto>> getForSelectedDay(
            @PathVariable Long diaryProfile,
            @PathVariable LocalDate selectedDay
    ){

        return  ResponseEntity.ok(
                sleepLogService
                        .getByDiaryProfileIdAndDate(
                                diaryProfile,
                                selectedDay
                        )
        );
    }

    @GetMapping("/diary-profile/{diaryProfileId}/for-a-period")
    public ResponseEntity<List<SleepLogDto>> getForSelectedPeriod(
            @PathVariable Long diaryProfileId,
            @Valid @ModelAttribute DateTimePeriodRequest request
    ){

        return ResponseEntity.ok(
                sleepLogService
                        .getByDiaryProfileAndPeriod(
                                diaryProfileId,
                                request.startPeriod(),
                                request.endPeriod()
                        )
        );
    }

    @GetMapping("/diary-profile/{diaryProfileId}/get-by-period")
    public ResponseEntity<List<WeightLogDto>> getByPeriod(
            @PathVariable Long diaryProfileId,
            @Valid @ModelAttribute DatePeriodRequest request
    ){

        return ResponseEntity.ok(weightLogService.getByDiaryProfileAndPeriod(
                diaryProfileId,
                request.startPeriod(),
                request.endPeriod()
        ));
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
}
