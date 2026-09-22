package com.github.plantfern.foodDiary.meals.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.meals.domain.services.MealTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/meals/diary-profile/{diaryProfileId}/templates")
public class MealTemplateController {

    private final MealTemplateService mealTemplateService;

    public MealTemplateController(MealTemplateService mealTemplateService) {
        this.mealTemplateService = mealTemplateService;
    }

    @PostMapping("")
    public ResponseEntity<Long> create(
            @PathVariable Long diaryProfileId,
            @RequestParam String name,
            @RequestParam LocalTime scheduledTime,
            @RequestParam Long frequency,
            @RequestParam Long startedAt
    ) {
        return ResponseEntity.ok(
                mealTemplateService.create(
                        diaryProfileId, name, scheduledTime, frequency, startedAt
                )
        );
    }

    @PutMapping("/{templateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable Long templateId,
            @RequestParam String name,
            @RequestParam LocalTime scheduledTime,
            @RequestParam Long frequency,
            @RequestParam Long startedAt
    ) {
        mealTemplateService.update(templateId, name, scheduledTime, frequency, startedAt);
    }

    @DeleteMapping("/{templateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@PathVariable Long templateId) {
        mealTemplateService.softDelete(templateId);
    }

    @PostMapping("/{templateId}/records")
    public ResponseEntity<Long> addRecord(
            @PathVariable Long templateId,
            @RequestParam Long servingId,
            @RequestParam Float amount
    ) {
        return ResponseEntity.ok(
                mealTemplateService.addRecord(templateId, servingId, amount)
        );
    }

    @PutMapping("/records/{templateRecordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRecord(
            @PathVariable Long templateRecordId,
            @RequestParam Float amount
    ) {
        mealTemplateService.updateRecord(templateRecordId, amount);
    }

    @DeleteMapping("/records/{templateRecordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecord(@PathVariable Long templateRecordId) {
        mealTemplateService.deleteRecord(templateRecordId);
    }
}
