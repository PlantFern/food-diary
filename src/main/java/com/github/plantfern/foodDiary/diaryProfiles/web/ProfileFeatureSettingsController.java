package com.github.plantfern.foodDiary.diaryProfiles.web;

import com.github.plantfern.foodDiary.diaryProfiles.domain.ProfileFeatureSettingsMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.dto.ProfileFeatureSettingsDto;
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
    private final ProfileFeatureSettingsMapper mapper;

    @Autowired
    public ProfileFeatureSettingsController(
            ProfileFeatureSettingsService profileFeatureSettingsService,
            ProfileFeatureSettingsMapper mapper) {
        this.profileFeatureSettingsService = profileFeatureSettingsService;
        this.mapper = mapper;
    }

    /** Актуальные (последние по createdAt) настройки профиля */
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

    /** Все настройки по diary profile */
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

    /** Все настройки, созданные пользователем */
    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<ProfileFeatureSettingsDto>> getAllByCreatedById(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                profileFeatureSettingsService.getAllByCreatedById(userId)
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }

    /** По создателю и diary profile */
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

    /**
     * По diary profile и диапазону дат
     * (expiredAt >= lowerBound OR createdAt < upperBound — как в сервисе/репозитории)
     */
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
}
