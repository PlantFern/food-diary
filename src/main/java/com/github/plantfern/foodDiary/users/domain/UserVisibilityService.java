package com.github.plantfern.foodDiary.users.domain;


import com.github.plantfern.foodDiary.users.api.VisibilityApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
class UserVisibilityService implements VisibilityApi {

    private final UserVisibilityRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean canSee(Long actorUserId, Long targetUserId) {
        return repository.existsByActorUserIdAndTargetUserId(actorUserId, targetUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExtended(Long actorUserId, Long targetUserId) {
        return repository.existsByIsExtendedTrueAndActorUserIdAndTargetUserId(
                actorUserId,
                targetUserId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasExtended(Long targetUserId){
        return repository.findByIsExtendedTrueAndTargetUserId(targetUserId);
    }
}
