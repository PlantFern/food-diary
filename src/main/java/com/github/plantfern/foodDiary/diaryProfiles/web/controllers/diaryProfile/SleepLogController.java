package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.SleepLogDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.SleepLogService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.DatePeriodRequest;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.DateTimePeriodRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/diary-profile/sleep-log")
public class SleepLogController {


    private final SleepLogService sleepLogService;

    public SleepLogController(SleepLogService sleepLogService) {
        this.sleepLogService = sleepLogService;
    }

    @PutMapping("/{diaryProfileId}")
    public ResponseEntity<SleepLogDto> create(
            @PathVariable Long diaryProfileId,
            @ModelAttribute DateTimePeriodRequest request
    ) {

        return ResponseEntity.ok(
                sleepLogService.create(
                        diaryProfileId,
                        request.startPeriod(),
                        request.endPeriod()
                )
        );
    }

    @PutMapping("/{sleepLogId}")
    public ResponseEntity<SleepLogDto> update(
            @PathVariable Long sleepLogId,
            @ModelAttribute DateTimePeriodRequest request
    ) {

        return ResponseEntity.ok(
                sleepLogService.update(
                    sleepLogId,
                    request.startPeriod(),
                    request.endPeriod()
                )
        );
    }

    @DeleteMapping("/{sleepLogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long sleepLogId
    ) {

        sleepLogService
                .delete(sleepLogId);
    }


    @GetMapping("/{sleepLogId}")
    public ResponseEntity<SleepLogDto> getById(
            @PathVariable Long sleepLogId
    ) {

        return ResponseEntity.ok(
                sleepLogService
                        .getById(sleepLogId)
        );
    }

}
