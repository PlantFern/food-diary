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
    @Column(name = "recordId")
    private Long recordId;

    @Column(name = "mealId")
    private Long mealId;

    @Column(name = "servingId")
    private Long servingId;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "eatenAt")
    private LocalTime eatenAt;

    @Column(name = "diaryProfileId")
    private Long diaryProfileId;

    @Column(name = "mealTypeId")
    private Long mealTypeId;

    @Column(name = "mealDate")
    private LocalDate mealDate;

    @Column(name = "generatedFromTemplateId")
    private Long generatedFromTemplateId;

    @Column(name = "photoPath")
    private String mealPhotoPath;

    @Column(name = "mealTypeCode")
    private String mealTypeCode;

    protected VMealDayRecordEntity() {}
}
