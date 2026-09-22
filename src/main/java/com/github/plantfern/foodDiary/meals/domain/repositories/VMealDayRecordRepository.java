package com.github.plantfern.foodDiary.meals.domain.repositories;


import com.github.plantfern.foodDiary.meals.domain.views.VMealDayRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VMealDayRecordRepository extends JpaRepository<VMealDayRecordEntity, Long> {

    List<VMealDayRecordEntity> findAllByDiaryProfileIdAndMealDate(
            Long diaryProfileId,
            LocalDate mealDate
    );
}


