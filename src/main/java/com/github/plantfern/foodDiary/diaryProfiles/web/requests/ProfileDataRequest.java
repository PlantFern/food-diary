package com.github.plantfern.foodDiary.diaryProfiles.web.requests;

import java.time.LocalDate;

public record ProfileDataRequest(
        Float height,
        LocalDate birthDate,
        Long genderCode
) {
}
