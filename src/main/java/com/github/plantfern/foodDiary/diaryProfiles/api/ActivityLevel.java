package com.github.plantfern.foodDiary.diaryProfiles.api;


import lombok.Getter;


public enum ActivityLevel {
    SEDENTARY(1.2F),
    LIGHT(1.375F),
    MODERATE(1.55F),
    HIGH(1.725F),
    VERY_HIGH(1.9F);

    @Getter
    private final float coefficient;

    ActivityLevel(float coefficient){
        this.coefficient = coefficient;
    }
}
