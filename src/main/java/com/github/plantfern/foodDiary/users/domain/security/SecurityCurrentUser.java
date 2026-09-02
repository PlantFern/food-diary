package com.github.plantfern.foodDiary.users.domain.security;

import com.github.plantfern.foodDiary.users.api.CurrentUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class SecurityCurrentUser implements CurrentUser {

    private SecurityCurrentUser(){}

    @Override
    public Long requireId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !(authentication.getPrincipal() instanceof SecurityUser user)){
            throw new AccessDeniedException("Access denied");
        }

        return user.getId();
    }
}
