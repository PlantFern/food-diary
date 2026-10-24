package com.github.plantfern.foodDiary.users.domain;


import com.github.plantfern.foodDiary.specialists.api.events.SpecialistDeletedEvent;
import com.github.plantfern.foodDiary.specialists.api.events.UserRelationActivated;
import com.github.plantfern.foodDiary.specialists.api.events.UserRelationInactivated;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SpecialistEventListener {

    private final UserVisibilityRepository userVisibilityRepository;


    @ApplicationModuleListener
    void onDeleted(SpecialistDeletedEvent event) {
        userVisibilityRepository.deleteByActorUserId(event.userId());
    }

    /**
     * ACTIVE relation → specialist may see diary owner (isExtended for SPECIALIST type only).
     * Without this row DiaryProfilePolicy / MealPolicy return 403.
     */
    @ApplicationModuleListener
    void onRelationActivated(UserRelationActivated event) {
        var existing = userVisibilityRepository.findByActorUserIdAndTargetUserId(
                event.specialistUserId(),
                event.diaryProfileUserId()
        );

        boolean extended = Boolean.TRUE.equals(event.isExtended());

        if (existing.isPresent()) {
            existing.get().setIsExtended(extended);
            userVisibilityRepository.save(existing.get());
        } else {
            userVisibilityRepository.save(new UserVisibilityEntity(
                    event.specialistUserId(),
                    event.diaryProfileUserId(),
                    extended
            ));
        }
    }

    @ApplicationModuleListener
    void onRelationInactivated(UserRelationInactivated event) {
        userVisibilityRepository.deleteByActorUserIdAndTargetUserId(
                event.specialistUserId(),
                event.diaryProfileUserId()
        );
    }
}
