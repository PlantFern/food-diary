package com.github.plantfern.foodDiary.meals.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.meals.domain.services.MealService;
import com.github.plantfern.foodDiary.meals.web.requests.MealTemplateApplyRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/diary-profile/{diaryProfileId}/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<Long> applyTemplate(
            @PathVariable Long diaryProfileId,
            @PathVariable Long templateId,
            @Valid @ModelAttribute MealTemplateApplyRequest request
    ) {
        return ResponseEntity.ok(
                mealService.applyTemplate(
                        diaryProfileId,
                        request.mealTypeId(),
                        templateId,
                        request.mealDate(),
                        request.eatenAt()
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