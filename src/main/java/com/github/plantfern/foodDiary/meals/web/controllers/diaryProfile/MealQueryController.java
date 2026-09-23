package com.github.plantfern.foodDiary.meals.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.meals.api.dto.DayMealsDto;
import com.github.plantfern.foodDiary.meals.domain.services.MealQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/meals/diary-profile/{diaryProfileId}")
public class MealQueryController {

    private final MealQueryService mealQueryService;

    public MealQueryController(MealQueryService mealQueryService) {
        this.mealQueryService = mealQueryService;
    }


    @GetMapping("/day")
    public DayMealsDto getDay(
            @PathVariable Long diaryProfileId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return mealQueryService.getDay(diaryProfileId, date);
    }
}