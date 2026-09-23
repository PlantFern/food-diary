package com.github.plantfern.foodDiary.food.domain.security;

import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;


@Component
public class FoodPolicy {

    public void ensureModeration(
            CurrentUser currentUser
    ) {

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        throw new AccessDeniedException("Only moderation has access");
    }

    public void ensureIsOwner(
            CurrentUser currentUser,
            Long ownerId
    ) {

        if(currentUser.requireId().equals(ownerId))
            return;

        throw new AccessDeniedException("Only owner has access");
    }

    public void ensureIsOwnerOrModeration(
            CurrentUser currentUser,
            Long ownerId
    ) {

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        if(currentUser.requireId().equals(ownerId))
            return;

        throw new AccessDeniedException("Only owner or moderation has access");
    }
}
