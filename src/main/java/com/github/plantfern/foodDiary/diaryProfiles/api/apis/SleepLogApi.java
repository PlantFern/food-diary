package com.github.plantfern.foodDiary.diaryProfiles.api.apis;

import com.github.plantfern.foodDiary.diaryProfiles.api.dto.SleepLogDto;

import java.time.LocalDate;
import java.util.List;

public interface SleepLogApi {

    List<SleepLogDto> getByDiaryProfileIdAndDateInternal(Long diaryProfileId, LocalDate interval);
}
