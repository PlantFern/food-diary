package com.github.plantfern.foodDiary.diaryProfiles.domain.security;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;
import com.github.plantfern.foodDiary.specialists.api.SpecialistApi;
import com.github.plantfern.foodDiary.specialists.api.UserRelationApi;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.api.VisibilityApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
@Component
public class DiaryProfilePolicy {

    private final VisibilityApi userVisibilityApi;

    public void ensureCanGet(
            CurrentUser currentUser,
            Long diaryProfileUserId
    ) {
        var currentUserId = currentUser.requireId();

        if(currentUserId.equals(diaryProfileUserId)){
            return;
        }

        if(currentUser.hasRole(RoleName.SPECIALIST)
            && userVisibilityApi.canSee(
                        currentUserId, diaryProfileUserId))
            return;

        if( currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        throw new AccessDeniedException("Access to diary profile denied");
    }

    public void ensureCanGetAll(
            CurrentUser currentUser,
            Collection<Long> diaryProfileUserIds
    ){
        var currentUserId = currentUser.requireId();

        if (currentUser.hasRole(RoleName.MODERATOR) ||
                currentUser.hasRole(RoleName.ADMINISTRATOR)) {
            return;
        }

        if (currentUser.hasRole(RoleName.SPECIALIST)){
            for (var id : diaryProfileUserIds) {
                boolean hasAccess = userVisibilityApi.canSee(
                        currentUserId, id
                );

                if (!hasAccess) {
                    throw new AccessDeniedException(
                            String.format("Access denied to diary profile with id: " + id)
                    );
                }
            }

            return;
        }

        throw new AccessDeniedException("No accessible diary profiles found");
    }


    public void ensureCanUpdate(
            CurrentUser currentUser,
            Long diaryProfileUserId
    ) {
        if(currentUser.requireId().equals(diaryProfileUserId)){
            return;
        }

        throw new AccessDeniedException("Only diary profile owner can update");
    }
}
