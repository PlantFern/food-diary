package com.github.plantfern.foodDiary.meals.domain;


import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.api.VisibilityApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class MealPolicy {

    private final VisibilityApi userVisibilityApi;

    public void ensureIsOwner(CurrentUser currentUser, Long ownerId){

        if(currentUser.requireId().equals(ownerId))
            return;

        throw new AccessDeniedException("Only owner has access");
    }

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
}
