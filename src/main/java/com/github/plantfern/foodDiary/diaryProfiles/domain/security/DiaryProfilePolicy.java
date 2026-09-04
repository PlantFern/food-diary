package com.github.plantfern.foodDiary.diaryProfiles.domain.security;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;
import com.github.plantfern.foodDiary.specialists.api.SpecialistApi;
import com.github.plantfern.foodDiary.specialists.api.UserRelationApi;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
@Component
public class DiaryProfilePolicy {

    private final CurrentUser currentUser;
    private final UserRelationApi userRelationApi;
    private final SpecialistApi specialistApi;

    public void ensureCanGet(DiaryProfileEntity diaryProfile) {
        var currentUserId = currentUser.requireId();

        if(currentUserId.equals(diaryProfile.getUserId())){
            return;
        }

        if((currentUser.hasRole(RoleName.OBSERVER) || currentUser.hasRole(RoleName.SPECIALIST))
            && userRelationApi
                .existsByDiaryProfileIdAndSpecialistId(
                        diaryProfile.getId(),
                        currentUserId
                )
        )
            return;

        if( currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        throw new AccessDeniedException(
                "Only owner, linked specialist/observer, or moderators can get diary profile"
        );
    }

    public void ensureCanGetAll(Collection<Long> ids){
        var currentUserId = currentUser.requireId();

        if (currentUser.hasRole(RoleName.MODERATOR) ||
                currentUser.hasRole(RoleName.ADMINISTRATOR)) {
            return;
        }

        if (currentUser.hasRole(RoleName.OBSERVER)
                || currentUser.hasRole(RoleName.SPECIALIST)){
            for (var id : ids) {
                boolean hasAccess = userRelationApi.existsByDiaryProfileIdAndSpecialistId(
                        id,
                        currentUserId
                );

                if (!hasAccess) {
                    throw new AccessDeniedException(
                            String.format("Access denied to diary profile with id: " + id)
                    );
                }
            }
        }

        throw new AccessDeniedException("No accessible diary profiles found");
    }


    public void ensureCanUpdate(Long targetUserId) {
        if(currentUser.requireId().equals(targetUserId)){
            return;
        }

        throw new AccessDeniedException("Only diary profile owner can update");
    }
}
