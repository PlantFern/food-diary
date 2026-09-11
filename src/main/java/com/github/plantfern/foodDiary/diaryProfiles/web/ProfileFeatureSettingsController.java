package com.github.plantfern.foodDiary.diaryProfiles.web;

import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.ProfileFeatureSettingsEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.services.ProfileFeatureSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/profile-feature-settings")
public class ProfileFeatureSettingsController {

    private final ProfileFeatureSettingsService profileFeatureSettingsService;

    @Autowired
    public ProfileFeatureSettingsController(
            ProfileFeatureSettingsService profileFeatureSettingsService
    ) {
        this.profileFeatureSettingsService = profileFeatureSettingsService;
    }

    /** Актуальные (последние по createdAt) настройки профиля */
    @GetMapping("/diary-profile/{diaryProfileId}/latest")
    public ResponseEntity<ProfileFeatureSettingsEntity> getLatestByDiaryProfileId(
            @PathVariable Long diaryProfileId
    ) {
        return ResponseEntity.ok(
                profileFeatureSettingsService
                        .getFirstByDiaryProfileIdOrderByCreatedAtDesc(diaryProfileId)
        );
    }

    /** Все настройки по diary profile */
    @GetMapping("/diary-profile/{diaryProfileId}")
    public ResponseEntity<List<ProfileFeatureSettingsEntity>> getAllByDiaryProfileId(
            @PathVariable Long diaryProfileId
    ) {
        return ResponseEntity.ok(
                profileFeatureSettingsService.getAllByDiaryProfileId(diaryProfileId)
        );
    }

    /** Все настройки, созданные пользователем */
    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<ProfileFeatureSettingsEntity>> getAllByCreatedById(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                profileFeatureSettingsService.getAllByCreatedById(userId)
        );
    }

    /** По создателю и diary profile */
    @GetMapping
    public ResponseEntity<List<ProfileFeatureSettingsEntity>> findAllByCreatedByIdAndDiaryProfileId(
            @RequestParam Long createdById,
            @RequestParam Long diaryProfileId
    ) {
        return ResponseEntity.ok(
                profileFeatureSettingsService.findAllByCreatedByIdAndDiaryProfileId(
                        createdById,
                        diaryProfileId
                )
        );
    }

    /**
     * По diary profile и диапазону дат
     * (expiredAt >= lowerBound OR createdAt < upperBound — как в сервисе/репозитории)
     */
    @GetMapping("/diary-profile/{diaryProfileId}/by-period")
    public ResponseEntity<List<ProfileFeatureSettingsEntity>> getAllByPeriodAndDiaryProfileId(
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
        );
    }
}
