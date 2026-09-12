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
        if(!userVisibilityRepository
                .existsByActorUserIdAndTargetUserId(
                        event.specialistUserId(),
                        event.diaryProfileUserId()
                )
        ) {
            var visibility = new UserVisibilityEntity();
            visibility.setActorUserId(event.specialistUserId());
            visibility.setTargetUserId(event.diaryProfileUserId());

            userVisibilityRepository.save(visibility);
        }
    }

    @ApplicationModuleListener
    void onInactivated(UserRelationInactivated event){
        userVisibilityRepository.deleteByActorUserIdAndTargetUserId(
                event.specialistUserId(),
                event.diaryProfileUserId()
        );
    }
}
