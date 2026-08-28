package com.github.plantfern.foodDiary;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest{
    static ApplicationModules modules = ApplicationModules.of(FoodDiaryApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
}