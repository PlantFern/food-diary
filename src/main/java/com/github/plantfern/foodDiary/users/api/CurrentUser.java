package com.github.plantfern.foodDiary.users.api;


public interface CurrentUser {
    Long requireId();
    boolean hasAuthority(String authority);
}
