package com.github.plantfern.foodDiary.meals.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.meals.domain.services.MealService;
import com.github.plantfern.foodDiary.meals.web.requests.MealCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;


@RestController
@RequestMapping("/api/meals/diary-profile/{diaryProfileId}")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<Long> applyTemplate(
            @PathVariable Long diaryProfileId,
            @PathVariable Long templateId,
            @RequestParam Long mealTypeId,
            @RequestParam java.time.LocalDate mealDate,
            @RequestParam LocalTime eatenAt
    ) {
        return ResponseEntity.ok(
                mealService.applyTemplate(
                        diaryProfileId,
                        mealTypeId,
                        templateId,
                        mealDate,
                        eatenAt
                )
        );
    }

    @PatchMapping("/{mealId}/photo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPhoto(
            @PathVariable Long mealId,
            @RequestParam String photoPath
    ) {
        mealService.update(mealId, null, photoPath);
    }

    @DeleteMapping("/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@PathVariable Long mealId) {
        mealService.softDelete(mealId);
    }
}