package com.github.plantfern.foodDiary.users.domain;


import com.github.plantfern.foodDiary.specialists.api.events.UserRelationActivated;
import com.github.plantfern.foodDiary.specialists.api.events.UserRelationInactivated;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
class UserVisibilityProjection {

    private final UserVisibilityRepository userVisibilityRepository;


    @ApplicationModuleListener
    void onActivated(UserRelationActivated event) {
        var existingOpt = userVisibilityRepository.findByActorUserIdAndTargetUserId(
                event.specialistUserId(),
                event.diaryProfileUserId()
        );

        if (existingOpt.isPresent()) {
            var visibility = existingOpt.get();
            visibility.setIsExtended(Boolean.TRUE.equals(event.isExtended()));
            userVisibilityRepository.save(visibility);
            return;
        }

        userVisibilityRepository.save(new UserVisibilityEntity(
                event.specialistUserId(),
                event.diaryProfileUserId(),
                Boolean.TRUE.equals(event.isExtended())
        ));
    }

    @ApplicationModuleListener
    void onInactivated(UserRelationInactivated event){
        userVisibilityRepository.deleteByActorUserIdAndTargetUserId(
                event.specialistUserId(),
                event.diaryProfileUserId()
        );
    }
}
