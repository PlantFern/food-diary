package com.github.plantfern.foodDiary.specialists.domain.security;


import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;


@Component
public class SpecialistPolicy {

    public void ensureCanModerate(CurrentUser currentUser){

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        throw new AccessDeniedException("Only moderators can get");
    }

    public void ensureIsOwner(
            CurrentUser currentUser
    ){

        if(!currentUser.hasRole(RoleName.SPECIALIST))
            throw new AccessDeniedException("Only user with SPECIALIST role can get");
    }
}
