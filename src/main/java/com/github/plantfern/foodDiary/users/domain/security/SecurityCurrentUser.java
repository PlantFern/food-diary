package com.github.plantfern.foodDiary.users.domain.security;

import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;


@Component
public class SecurityCurrentUser implements CurrentUser {

    public SecurityCurrentUser(){}

    @Override
    public boolean hasRole(RoleName role){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(
                        a -> Objects.equals(a.getAuthority(), "ROLE_" + role.name())
                );
    }

    @Override
    public Long requireId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !(authentication.getPrincipal() instanceof SecurityUser user)){
            throw new AccessDeniedException("Access denied");
        }

        return user.getId();
    }
}
