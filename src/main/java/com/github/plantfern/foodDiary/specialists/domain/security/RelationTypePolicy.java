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

        if(currentUser.requireId().equals(targetRelation.getSpecialist().getUserId()))
            return;

        if(currentUser.hasRole(RoleName.MODERATOR)
        || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        throw new AccessDeniedException("Only users related to relation or moderators can get");
    }

    public void ensureCanActivateOrReject(
            CurrentUser currentUser,
            String relationStatus,
            Long specialistUserId){

        ensureCanChangeStatus(currentUser, relationStatus, UserRelationStatus.PENDING, specialistUserId);

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;
    }

    public void ensureCanEnd(
            CurrentUser currentUser,
            String relationStatus,
            Long specialistUserId){

        ensureCanChangeStatus(currentUser, relationStatus, UserRelationStatus.ACTIVE, specialistUserId);

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;
    }

    public void ensureCanChangeStatus(
            CurrentUser currentUser,
            String currentRelationStatus,
            UserRelationStatus targetRelationStatus,
            Long specialistUserId
    ){

        if(currentUser.requireId().equals(specialistUserId))
            return;

        if(!UserRelationStatus
                .valueOf(currentRelationStatus)
                .equals(targetRelationStatus)){
            throw new IllegalStateException("User relation status must be pending");
        }

        throw new AccessDeniedException("Only specialist or moderator can change status of user relation");
    }

    public void ensureCanCreate(
            CurrentUser currentUser,
            Long diaryProfileUserId,
            Long specialistId
    ) {
        var currentId = currentUser.requireId();

        if(currentId.equals(diaryProfileUserId) || currentId.equals(specialistId))
            return;

        throw new AccessDeniedException("Only the users participating in the relation can create it");
    }
}
