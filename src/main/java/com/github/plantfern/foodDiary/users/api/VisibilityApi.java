package com.github.plantfern.foodDiary.users.api;

public interface VisibilityApi {
    boolean canSee(Long actorUserId, Long targetUserId);
}
