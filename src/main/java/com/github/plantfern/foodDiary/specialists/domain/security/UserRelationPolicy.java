package com.github.plantfern.foodDiary.specialists.domain.security;

import com.github.plantfern.foodDiary.specialists.api.UserRelationStatus;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.RoleName;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;


@Component
public class UserRelationPolicy {

    public void ensureCanGetAll(
            CurrentUser currentUser
    ){

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        throw new AccessDeniedException("Only moderators can get");
    }

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
            Long specialistUserId
    ) {

        if(currentUser.requireId().equals(specialistUserId))
            return;

        if(!UserRelationStatus
                .valueOf(relationStatus)
                .equals(UserRelationStatus.PENDING)){
            throw new IllegalStateException("User relation status must be pending");
        }

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;
    }

    public void ensureCanEnd(
            CurrentUser currentUser,
            String relationStatus,
            Long specialistUserId,
            Long diaryProfileUserId
    ) {

        var currentId = currentUser.requireId();

        if(currentId.equals(diaryProfileUserId) || currentId.equals(specialistUserId))
            return;

        if(!UserRelationStatus
                .valueOf(relationStatus)
                .equals(UserRelationStatus.ACTIVE)){
            throw new IllegalStateException("User relation status must be pending");
        }

        throw new AccessDeniedException("Only users related to user relation can access");
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

    public void ensureSpecialistAccess(
            CurrentUser currentUser,
            Long specialistUserId
    ) {

        var currentId = currentUser.requireId();

        if(currentId.equals(specialistUserId))
            return;

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        throw new AccessDeniedException("Only specialist or moderators can get");
    }

    public void ensureClientAccess(
            CurrentUser currentUser,
            Long diaryProfileUserId
    ) {

        var currentId = currentUser.requireId();

        if(currentId.equals(diaryProfileUserId))
            return;

        if(currentUser.hasRole(RoleName.MODERATOR)
                || currentUser.hasRole(RoleName.ADMINISTRATOR))
            return;

        throw new AccessDeniedException("Only diary profile user or moderators can get");
    }

    public void ensureCanCreate(
            CurrentUser currentUser,
            Long diaryProfileUserId,
            Long specialistUserId
    ) {
        var currentId = currentUser.requireId();

        if(currentId.equals(diaryProfileUserId) || currentId.equals(specialistUserId))
            return;

        if(diaryProfileUserId.equals(specialistUserId))
            throw new IllegalStateException("User cannot create relations with themselves");

        throw new AccessDeniedException("Only the users participating in the relation can create it");
    }
}
