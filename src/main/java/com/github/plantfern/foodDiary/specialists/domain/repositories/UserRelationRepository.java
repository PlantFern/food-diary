package com.github.plantfern.foodDiary.specialists.domain.repositories;


import com.github.plantfern.foodDiary.specialists.domain.RelationType;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationEntity;
import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRelationRepository extends JpaRepository<UserRelationEntity, Long> {
    List<UserRelationEntity> findAllBySpecialistId(Long specialistId);

    List<UserRelationEntity> findAllBySpecialistIdAndRelationType(Long specialistId, RelationType relationType);
    List<UserRelationEntity> findAllBySpecialistIdAndRelationTypeAndUserRelationStatusEntityIs(
            Long specialistId,
            RelationType relationType,
            UserRelationStatusEntity userRelationStatusEntity);

    List<UserRelationEntity> findAllByDiaryProfileId(Long profileId);

    List<Integer> countDiaryProfileIdAndSpecialistId(Long specialistId);
}
