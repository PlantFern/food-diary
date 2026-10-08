package com.github.plantfern.foodDiary.users.domain;


import com.github.plantfern.foodDiary.diaryProfiles.api.events.DiaryProfileDeletedEvent;
import com.github.plantfern.foodDiary.specialists.api.events.SpecialistDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SpecialistEventListener {

    private final UserVisibilityRepository  userVisibilityRepository;


    @ApplicationModuleListener
    void onDeleted(SpecialistDeletedEvent event){

        userVisibilityRepository.deleteByActorUserId(event.userId());
    }
}
