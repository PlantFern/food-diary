package com.github.plantfern.foodDiary.users.domain;


import com.github.plantfern.foodDiary.diaryProfiles.api.events.DiaryProfileDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DiaryEventListener {

    private final UserVisibilityRepository  userVisibilityRepository;


    @ApplicationModuleListener
    void onDeleted(DiaryProfileDeletedEvent event){

        userVisibilityRepository.deleteByTargetUserId(event.userId());
    }
}
