package com.github.plantfern.foodDiary.diaryProfiles.domain.security;


import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.api.VisibilityApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Collection;


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

        throw new AccessDeniedException(
                "Access to diary profile resource denied"
        );
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

        throw new AccessDeniedException(
                "Access to diary profile resources denied"
        );
    }


    public void ensureIsOwner(CurrentUser currentUser, Long ownerId){

        if(currentUser.requireId().equals(ownerId))
            return;

        throw new AccessDeniedException("Only owner has access");
    }

    public void ensureCanWrite(CurrentUser currentUser, Long diaryProfileOwnerUserId) {
        var currentId = currentUser.requireId();
        boolean owner = currentId.equals(diaryProfileOwnerUserId);

        if (owner) {
            if (userVisibilityApi.hasExtended(diaryProfileOwnerUserId)) {
                throw new AccessDeniedException(
                        "Diary profile owner cannot modify resources while an extended specialist relation is active"
                );
            }
            return;
        }

        if (currentUser.hasRole(RoleName.SPECIALIST)
                && userVisibilityApi.canSee(currentId, diaryProfileOwnerUserId)
                && userVisibilityApi.isExtended(currentId, diaryProfileOwnerUserId)) {
            return;
        }

        throw new AccessDeniedException(
                "Only the diary owner (without an extended specialist) or their extended specialist can modify this resource"
        );
    }

    public void ensureCreatedBy(
            CurrentUser currentUser,
            Long createdById
    ) {

        if(currentUser.requireId().equals(createdById))
            return;

        throw new AccessDeniedException("No access to parent record");
    }
}
