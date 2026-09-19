package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
