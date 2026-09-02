package com.github.plantfern.foodDiary.users.domain.security;


import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;


@Component
public class UserGettingPolicy {
    public void ensureCanGet(
            UserEntity actorUser,
            UserEntity targetUser
    ){
        if (targetUser.getId().equals(actorUser.getId()))
            return;

        if (actorUser.roleNames().contains(RoleName.MODERATOR)
                || actorUser.roleNames().contains(RoleName.ADMINISTRATOR)){
            return;
        }

        throw new AccessDeniedException("Only moderator or administrator can get other users data");
    }
}
