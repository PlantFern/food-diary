package com.github.plantfern.foodDiary.diaryProfiles.web.controllers.diaryProfile;


import com.github.plantfern.foodDiary.diaryProfiles.api.dto.ProfileFeatureSettingsDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.mappers.ProfileFeatureSettingsMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.ProfileFeatureSettingsService;
import com.github.plantfern.foodDiary.diaryProfiles.web.requests.ProfileFeatureSettingsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/diary-profile/feature-settings")
public class ProfileFeatureSettingsController {

    private final ProfileFeatureSettingsService profileFeatureSettingsService;
    private final ProfileFeatureSettingsMapper mapper;

    @Autowired
    public ProfileFeatureSettingsController(
            ProfileFeatureSettingsService profileFeatureSettingsService,
            ProfileFeatureSettingsMapper mapper) {
        this.profileFeatureSettingsService = profileFeatureSettingsService;
        this.mapper = mapper;
    }


    @PutMapping("/{diaryProfileId}")
    public ResponseEntity<ProfileFeatureSettingsDto> create(
            @PathVariable Long diaryProfileId,
            @RequestParam ProfileFeatureSettingsRequest request
    ) {

        return ResponseEntity.ok(
                profileFeatureSettingsService.create(
                        diaryProfileId,
                        request.showSleep(),
                        request.showSleepLogs(),
                        request.showWeight(),
                        request.showSleepLogs(),
                        request.showAllergensWarning(),
                        request.hiddenNutrients()
                )
        );
    }

    @PostMapping("/{profileFeatureSettingsId}")
    public ResponseEntity<ProfileFeatureSettingsDto> update(
            @PathVariable Long profileFeatureSettingsId,
            @RequestParam ProfileFeatureSettingsRequest request
    ) {

        return ResponseEntity.ok(
                profileFeatureSettingsService.update(
                        profileFeatureSettingsId,
                        request.showSleep(),
                        request.showSleepLogs(),
                        request.showWeight(),
                        request.showSleepLogs(),
                        request.showAllergensWarning(),
                        request.hiddenNutrients()
                )
        );
    }

    @PatchMapping("/complete/{profileFeatureSettingsId}")
    public ResponseEntity<ProfileFeatureSettingsDto> complete(
            @PathVariable Long profileFeatureSettingsId
    ) {

        return ResponseEntity.ok(
                profileFeatureSettingsService.complete(profileFeatureSettingsId)
        );
    }

    @DeleteMapping("/{profileFeatureSettingsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long profileFeatureSettingsId) {

        profileFeatureSettingsService.delete(profileFeatureSettingsId);
    }


    @GetMapping("/diary-profile/{diaryProfileId}/latest")
    public ResponseEntity<ProfileFeatureSettingsDto> getLatestByDiaryProfileId(
            @PathVariable Long diaryProfileId
    ) {
        return ResponseEntity.ok(
                mapper.toDto(
                        profileFeatureSettingsService
                                .getFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
                )
        );
    }

    @GetMapping("/diary-profile/{diaryProfileId}")
    public ResponseEntity<List<ProfileFeatureSettingsDto>> getAllByDiaryProfileId(
            @PathVariable Long diaryProfileId
    ) {
        return ResponseEntity.ok(
                profileFeatureSettingsService.getAllByDiaryProfileId(diaryProfileId)
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/diary-profile/{diaryProfileId}/by-period")
    public ResponseEntity<List<ProfileFeatureSettingsDto>> getAllByPeriodAndDiaryProfileId(
            @PathVariable Long diaryProfileId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime lowerBound,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime upperBound
    ) {
        return ResponseEntity.ok(
                profileFeatureSettingsService
                        .getAllByExpiredAtBetweenOrCreatedAtLessThanAndDiaryProfileId(
                                lowerBound,
                                upperBound,
                                diaryProfileId
                        )
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }

    @GetMapping
    public ResponseEntity<List<ProfileFeatureSettingsDto>> findAllByCreatedByIdAndDiaryProfileId(
            @RequestParam Long createdById,
            @RequestParam Long diaryProfileId
    ) {
        return ResponseEntity.ok(
                profileFeatureSettingsService.findAllByCreatedByIdAndDiaryProfileId(
                                createdById,
                                diaryProfileId
                        )
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }

    @GetMapping
    public ResponseEntity<ProfileFeatureSettingsDto> getById(
            @RequestParam Long profileFeatureSettings
    ) {
        return ResponseEntity.ok(mapper.toDto(
                        profileFeatureSettingsService.getById(
                                profileFeatureSettings
                                )
                )
        );
    }
}
