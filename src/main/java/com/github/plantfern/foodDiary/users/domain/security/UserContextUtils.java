package com.github.plantfern.foodDiary.users.domain.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class UserContextUtils {

    private UserContextUtils(){}

    public static Long getCurrentUserIdOrThrow(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !(authentication.getPrincipal() instanceof SecurityUser user)){
            throw new AccessDeniedException("Access denied");
        }

        return user.getId();
    }
}
