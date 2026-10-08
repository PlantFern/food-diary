package com.github.plantfern.foodDiary.specialists.domain;


import com.github.plantfern.foodDiary.diaryProfiles.api.events.DiaryProfileDeletedEvent;
import com.github.plantfern.foodDiary.specialists.api.UserRelationStatus;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.specialists.domain.repositories.UserRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DiaryEventListener {

    private final UserRelationRepository userRelationRepository;


    @ApplicationModuleListener
    void onDeleted(DiaryProfileDeletedEvent event) {

        var userRelations = userRelationRepository
                .findAllByDiaryProfileId(event.diaryProfileId());

        if (userRelations.isEmpty()) return;
        for (UserRelationEntity userRelation : userRelations) {

            if(userRelation.getUserRelationStatusEntity().getCode().equals(UserRelationStatus.PENDING.name())){
                userRelation.getUserRelationStatusEntity().setCode(UserRelationStatus.CANCELED.name());
            }

            if(userRelation.getUserRelationStatusEntity().getCode().equals(UserRelationStatus.ACTIVE.name())){
                userRelation.getUserRelationStatusEntity().setCode(UserRelationStatus.ENDED.name());
            }
        }
    }
}
