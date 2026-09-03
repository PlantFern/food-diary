package com.github.plantfern.foodDiary.specialists.domain.repositories;

import com.github.plantfern.foodDiary.specialists.domain.entities.UserRelationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRelationStatusRepository extends JpaRepository<UserRelationStatusEntity, Long> {
    UserRelationStatusEntity getByCode(String code);
}
