package com.github.plantfern.foodDiary.users.domain.security;


import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.domain.UserVisibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserPolicy {

    private final UserVisibilityRepository visibilityRepository;


    public void ensureCanGet(
            CurrentUser actorUser,
            Long targetUserId
    ){
        if (targetUserId.equals(actorUser.requireId()))
            return;

        if (actorUser.hasRole(RoleName.ADMINISTRATOR)
            || actorUser.hasRole(RoleName.MODERATOR))
            return;

        if (actorUser.hasRole(RoleName.SPECIALIST)
            && visibilityRepository
                .existsByActorUserIdAndTargetUserId(actorUser.requireId(), targetUserId))
            return;

        throw new AccessDeniedException("Only moderator or administrator can get other users data");
    }
}
