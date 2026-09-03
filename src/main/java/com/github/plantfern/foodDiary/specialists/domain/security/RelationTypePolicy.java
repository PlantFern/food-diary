package com.github.plantfern.foodDiary.specialists.domain.security;

import com.github.plantfern.foodDiary.specialists.domain.UserRelationStatus;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class RelationTypePolicy {

    public void ensureCanGet(
            CurrentUser currentUser,
            UserRelationEntity targetRelation,
            Long diaryProfileUserId
            ){
        if(currentUser.requireId().equals(diaryProfileUserId))
            return;

        if(currentUser.requireId().equals(targetRelation.getSpecialistId()))
            return;

        if(currentUser.hasRole(RoleName.MODERATOR)
        || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        throw new AccessDeniedException("Only users related to relation or moderators can get");
    }

    public void ensureCanActivateOrReject(
            CurrentUser currentUser,
            UserRelationEntity targetRelation){

        ensureCanChangeStatus(currentUser, targetRelation);

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        if(!UserRelationStatus
                .valueOf(targetRelation.getUserRelationStatusEntity().getCode())
                .equals(UserRelationStatus.PENDING)){
            throw new IllegalStateException("User relation status must be pending");
        }
    }

    public void ensureCanEnd(
            CurrentUser currentUser,
            UserRelationEntity targetRelation){

        ensureCanChangeStatus(currentUser, targetRelation);

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        if(!UserRelationStatus
                .valueOf(targetRelation.getUserRelationStatusEntity().getCode())
                .equals(UserRelationStatus.ACTIVE)){
            throw new IllegalStateException("User relation status must be pending");
        }
    }

    public void ensureCanChangeStatus(
            CurrentUser currentUser,
            UserRelationEntity targetRelation){

        if(currentUser.requireId().equals(targetRelation.getSpecialistId()))
            return;

        if(!UserRelationStatus
                .valueOf(targetRelation.getUserRelationStatusEntity().getCode())
                .equals(UserRelationStatus.PENDING)){
            throw new IllegalStateException("User relation status must be pending");
        }

        throw new AccessDeniedException("Only specialist or moderator can change status of user relation");
    }

    public void ensureCanCreate(
            CurrentUser currentUser,
            Long diaryProfileUserId,
            Long specialistId
    ) {
        if(currentUser.requireId().equals(specialistId))
            return;

        if(currentUser.requireId().equals(diaryProfileUserId))
            return;

        throw new AccessDeniedException("Only the users participating in the relation can create it");
    }
}
