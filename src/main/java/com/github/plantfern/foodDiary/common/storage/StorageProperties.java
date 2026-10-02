package com.github.plantfern.foodDiary.common.storage;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties(prefix="app.storage")
public record StorageProperties(
        String location,
        long maxSize,
        List<String> allowTypes
) {
}
