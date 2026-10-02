package com.github.plantfern.foodDiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan("com.github.plantfern.foodDiary.common.storage")
public class FoodDiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDiaryApplication.class, args);
    }

}
