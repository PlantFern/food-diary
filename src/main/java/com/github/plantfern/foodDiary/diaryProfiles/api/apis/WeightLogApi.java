package com.github.plantfern.foodDiary.diaryProfiles.api.apis;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.WeightLogDto;

public interface WeightLogApi {

    WeightLogDto getLatestByDiaryProfileIdInternal(Long diaryProfileId);
}
