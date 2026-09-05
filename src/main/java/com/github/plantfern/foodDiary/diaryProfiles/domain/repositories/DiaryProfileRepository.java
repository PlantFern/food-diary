package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;

import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DiaryProfileRepository extends JpaRepository<DiaryProfileEntity, Long> {
    boolean existsByUserId(Long userId);

    @Query("""
        select dp.userId
        from DiaryProfileEntity dp
        where dp.id = :id
        """)
    Long findUserIdById(@Param("id") Long id);
}
