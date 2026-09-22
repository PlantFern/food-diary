package com.github.plantfern.foodDiary.meals.domain.views;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter

@Entity
@Immutable
@Table(name = "v_meal_day_records")
public class VMealDayRecordEntity {

    @Id
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "meal_id")
    private Long mealId;

    @Column(name = "serving_id")
    private Long servingId;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "eaten_at")
    private LocalTime eatenAt;

    @Column(name = "diary_profile_id")
    private Long diaryProfileId;

    @Column(name = "meal_type_id")
    private Long mealTypeId;

    @Column(name = "meal_date")
    private LocalDate mealDate;

    @Column(name = "generated_from_template_id")
    private Long generatedFromTemplateId;

    @Column(name = "meal_photo_path")
    private String mealPhotoPath;

    @Column(name = "meal_type_code")
    private String mealTypeCode;

    protected VMealDayRecordEntity() {}
}

