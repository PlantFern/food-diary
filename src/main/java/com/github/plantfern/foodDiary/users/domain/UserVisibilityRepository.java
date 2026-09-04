package com.github.plantfern.foodDiary.users.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserVisibilityRepository extends JpaRepository<UserVisibilityEntity, Long> {
    boolean existsByActorUserIdAndTargetUserId(Long actorUserId, Long targetUserId);

    void deleteByActorUserIdAndTargetUserId(Long actorUserId, Long targetUserId);
}
