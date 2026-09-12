package com.github.plantfern.foodDiary.users.domain.security;


import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class RoleAssignmentPolicy {


    public void ensureCanAssign(
            CurrentUser actorUser,
            Long targetUserId,
            Set<RoleName> requestedRoles
    ){

        if (requestedRoles == null || requestedRoles.isEmpty()) {
            throw new IllegalArgumentException("Roles can't be null or empty");
        }

        if(actorUser.requireId().equals(targetUserId)) {
            ensureRequestedAreAllowed(
                    requestedRoles,
                    Set.of(
                            RoleName.SPECIALIST
                    )
            );
            return;
        }

        if ( actorUser.hasRole(RoleName.ADMINISTRATOR)) {
            this.ensureRequestedAreAllowed(
                    requestedRoles,
                    Set.of(
                            RoleName.ADMINISTRATOR,
                            RoleName.MODERATOR
                    )
            );
            return;
        }

        if ( actorUser.hasRole(RoleName.MODERATOR)) {
            throw new AccessDeniedException("Moderator can't assign roles");
        }
    }


    private void ensureRequestedAreAllowed(Set<RoleName> requiredRoles, Set<RoleName> allowedRoles){
        if(!allowedRoles.containsAll(requiredRoles)){
            throw new AccessDeniedException("One or more requested roles are not allowed");
        }
    }
}
