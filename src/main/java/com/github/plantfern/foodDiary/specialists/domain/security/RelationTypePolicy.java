package com.github.plantfern.foodDiary.specialists.domain.security;

import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.users.api.RoleName;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class RelationTypePolicy {

    public void ensureCanGet(
            Long actorUserId,
            UserRelationEntity targetRelation,
            Set<RoleName> roles){
        if(actorUserId.equals(targetRelation.getDiaryProfileId()))
            return;

        if(actorUserId.equals(targetRelation.getSpecialistId()))
            return;

        if(roles.contains(RoleName.MODERATOR))
            return;
    }

    public void ensureCanUpdate(
            Long actorUserId,
            UserRelationEntity targetRelation){
        if(actorUserId.equals(targetRelation.getDiaryProfileId()))
            return;

        if(actorUserId.equals(targetRelation.getSpecialistId()))
            return;

        throw new AccessDeniedException("Only users related to relation or moderators can update");
    }
}
