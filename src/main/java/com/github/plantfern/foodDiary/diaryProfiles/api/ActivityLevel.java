package com.github.plantfern.foodDiary.diaryProfiles.api;


import lombok.Getter;


public enum ActivityLevel {
    SEDENTARY(1.2),
    LIGHT(1.375),
    MODERATE(1.55),
    HIGH(1.725),
    VERY_HIGH(1.9);

    @Getter
    private final double coefficient;

    ActivityLevel(double coefficient){
        this.coefficient = coefficient;
    }
}
