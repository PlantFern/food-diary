package com.github.plantfern.foodDiary.diaryProfiles.domain.repositories;


import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.GenderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GenderRepository extends JpaRepository<GenderEntity, Long> {
}
