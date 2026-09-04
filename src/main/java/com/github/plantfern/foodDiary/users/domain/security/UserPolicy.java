package com.github.plantfern.foodDiary.users.domain.security;


import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.domain.UserVisibilityRepository;
import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserPolicy {

    private final CurrentUser currentUser;
    private final UserVisibilityRepository visibilityRepository;

    public void ensureCanGet(
            UserEntity actorUser,
            UserEntity targetUser
    ){
        if (targetUser.getId().equals(actorUser.getId()))
            return;

        if (currentUser.hasRole(RoleName.ADMINISTRATOR)
            || currentUser.hasRole(RoleName.MODERATOR))
            return;

        if(currentUser.hasRole(RoleName.SPECIALIST)
            || (currentUser.hasRole(RoleName.OBSERVER)
            && visibilityRepository
                .existsByActorUserIdAndTargetUserId(actorUser.getId(), targetUser.getId())))
            return;

        throw new AccessDeniedException("Only moderator or administrator can get other users data");
    }
}
