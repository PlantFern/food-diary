package com.github.plantfern.foodDiary.diaryProfiles.api;


import lombok.Getter;


public enum GoalType {
    LOSE_WEIGHT(-0.20F, 0.35F, 0.30F, 0.35F),
    MAINTAIN(0F, 0.25F, 0.30F, 0.45F),
    GAIN_WEIGHT(0.15F, 0.25F, 0.25F, 0.50F);

    @Getter
    private final float calorieAdjustment;

    @Getter
    private final float proteinRatio;

    @Getter
    private final float fatRation;

    @Getter
    private final float carbRatio;

    GoalType(
            float calorieAdjustment,
            float proteinRatio,
            float fatRation,
            float carbRatio
    ) {
        this.calorieAdjustment = calorieAdjustment;
        this.proteinRatio = proteinRatio;
        this.fatRation = fatRation;
        this.carbRatio = carbRatio;
    }
}
