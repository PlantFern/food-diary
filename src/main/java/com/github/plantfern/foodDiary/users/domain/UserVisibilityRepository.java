package com.github.plantfern.foodDiary.users.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserVisibilityRepository extends JpaRepository<UserVisibilityEntity, Long> {
    boolean existsByActorUserIdAndTargetUserId(Long actorUserId, Long targetUserId);

    Optional<UserVisibilityEntity> findByActorUserIdAndTargetUserId(Long actorUserId, Long targetUserId);

    void deleteByActorUserIdAndTargetUserId(Long actorUserId, Long targetUserId);

    void deleteByTargetUserId(Long targetUserId);
    void deleteByActorUserId(Long actorUserId);

    boolean existsByIsExtendedTrueAndActorUserIdAndTargetUserId(
            Long actorUserId,
            Long targetUserId);

    /** Returns false when no row — never null (avoids NPE on primitive boolean). */
    boolean existsByIsExtendedTrueAndTargetUserId(Long targetUserId);
}
