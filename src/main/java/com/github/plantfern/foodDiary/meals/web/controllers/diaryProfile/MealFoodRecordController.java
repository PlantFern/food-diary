package com.github.plantfern.foodDiary.meals.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.meals.domain.services.MealFoodRecordService;
import com.github.plantfern.foodDiary.meals.web.requests.MealFoodRecordCreateRequest;
import com.github.plantfern.foodDiary.meals.web.requests.QuickProductCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;


@RestController
@RequestMapping("/api/meals/diary-profile/{diaryProfileId}/records")
public class MealFoodRecordController {

    private final MealFoodRecordService mealFoodRecordService;

    public MealFoodRecordController(MealFoodRecordService mealFoodRecordService) {
        this.mealFoodRecordService = mealFoodRecordService;
    }

    @PostMapping("/")
    public ResponseEntity<Long> add(
            @PathVariable Long diaryProfileId,
            @RequestBody MealFoodRecordCreateRequest request
    ) {
        return ResponseEntity.ok(
                mealFoodRecordService.add(
                        diaryProfileId,
                        request.mealId(),
                        request.mealTypeId(),
                        request.date(),
                        request.servingId(),
                        request.amount(),
                        request.eatenAt()
                )
        );
    }

    @PostMapping("/quick-product")
    public ResponseEntity<Long> addWithQuickProduct(
            @PathVariable Long diaryProfileId,
            @RequestBody QuickProductCreateRequest request
    ) {
        return ResponseEntity.ok(
                mealFoodRecordService.addWithQuickProduct(
                        diaryProfileId,
                        request.mealId(),
                        request.mealTypeId(),
                        request.date(),
                        request.eatenAt(),
                        request.amount(),
                        request.description(),
                        request.gramWeight(),
                        request.nutrients()
                )
        );
    }

    @PutMapping("/{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable Long recordId,
            @RequestParam(required = false) Float amount,
            @RequestParam(required = false) LocalTime eatenAt
    ) {
        mealFoodRecordService.update(recordId, amount, eatenAt);
    }

    @DeleteMapping("/{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long recordId) {
        mealFoodRecordService.delete(recordId);
    }
}
