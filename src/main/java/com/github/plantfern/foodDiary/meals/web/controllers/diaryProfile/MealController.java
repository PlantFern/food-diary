package com.github.plantfern.foodDiary.meals.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.common.storage.FileService;
import com.github.plantfern.foodDiary.meals.domain.services.MealService;
import com.github.plantfern.foodDiary.meals.web.requests.MealTemplateApplyRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Validated
@RestController
@RequestMapping("/api/diary-profile/{diaryProfileId}/meals")
public class MealController {

    private final MealService mealService;
    private final FileService fileService;

    public MealController(MealService mealService, FileService fileService) {
        this.mealService = mealService;
        this.fileService = fileService;
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

    @PatchMapping(value = "/{mealId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> setPhoto(
            @PathVariable Long diaryProfileId,
            @PathVariable Long mealId,
            @RequestParam("file") MultipartFile file
    ) {
        var uploaded = fileService.upload(file);
        String path = "/api/files/" + uploaded.getId();
        mealService.updatePhoto(mealId, path);
        return ResponseEntity.ok(path);
    }

    @DeleteMapping("/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long mealId) {
        mealService.softDelete(mealId);
    }
}
