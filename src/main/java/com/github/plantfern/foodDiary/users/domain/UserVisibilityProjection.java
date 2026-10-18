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
        var existing = userVisibilityRepository
                .findAll()
                .stream()
                .filter(v ->
                        event.specialistUserId().equals(v.getActorUserId())
                                && event.diaryProfileUserId().equals(v.getTargetUserId()))
                .findFirst();

        if (existing.isPresent()) {
            var visibility = existing.get();
            visibility.setIsExtended(Boolean.TRUE.equals(event.isExtended()));
            userVisibilityRepository.save(visibility);
            return;
        }

        var visibility = new UserVisibilityEntity(
                event.specialistUserId(),
                event.diaryProfileUserId(),
                Boolean.TRUE.equals(event.isExtended())
        );
        userVisibilityRepository.save(visibility);
    }

    @ApplicationModuleListener
    void onInactivated(UserRelationInactivated event){
        userVisibilityRepository.deleteByActorUserIdAndTargetUserId(
                event.specialistUserId(),
                event.diaryProfileUserId()
        );
    }
}
