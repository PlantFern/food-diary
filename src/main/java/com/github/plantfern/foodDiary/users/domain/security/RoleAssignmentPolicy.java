package com.github.plantfern.foodDiary.users.domain.security;


import com.github.plantfern.foodDiary.users.api.RoleName;
import com.github.plantfern.foodDiary.users.domain.entities.UserEntity;
import com.github.plantfern.foodDiary.users.domain.entities.UserRoleEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
public class RoleAssignmentPolicy {


    public void ensureCanAssign(
            UserEntity actorUser,
            UserEntity targetUser,
            Set<RoleName> requestedRoles
    ){

        if (requestedRoles == null || requestedRoles.isEmpty()) {
            throw new IllegalArgumentException("Roles can't be null or empty");
        }

        Set<RoleName> actorUserRoles = actorUser.roleNames();

        if(actorUser.getId().equals(targetUser.getId())) {
            ensureRequestedAreAllowed(
                    requestedRoles,
                    Set.of(
                            RoleName.SPECIALIST
                    )
            );
            return;
        }

        if (actorUser.roleNames().contains(RoleName.ADMINISTRATOR)) {
            this.ensureRequestedAreAllowed(
                    requestedRoles,
                    Set.of(
                            RoleName.ADMINISTRATOR,
                            RoleName.MODERATOR
                    )
            );
            return;
        }

        if (actorUser.roleNames().contains(RoleName.MODERATOR)) {
            throw new AccessDeniedException("Moderator can't assign roles");
        }
    }


    private void ensureRequestedAreAllowed(Set<RoleName> requiredRoles, Set<RoleName> allowedRoles){
        if(!allowedRoles.containsAll(requiredRoles)){
            throw new AccessDeniedException("One or more requested roles are not allowed");
        }
    }
}
