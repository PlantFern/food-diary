package com.github.plantfern.foodDiary.diaryProfiles.api.apis;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;
import org.springframework.lang.Nullable;

public interface WeightLogApi {

    /** Latest weight log or null if the profile has none yet. */
    @Nullable
    WeightLogDto getLatestByDiaryProfileIdInternal(Long diaryProfileId);
}
