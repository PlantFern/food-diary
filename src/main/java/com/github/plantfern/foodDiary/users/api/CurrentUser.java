package com.github.plantfern.foodDiary.users.api;


import java.util.Set;

public interface CurrentUser {
    Long requireId();
    boolean hasRole(RoleName role);
}
