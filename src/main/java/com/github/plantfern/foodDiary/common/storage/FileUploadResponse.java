package com.github.plantfern.foodDiary.common.storage;

public record FileUploadResponse(
        Long id,
        String url,
        String originalName,
        String contentType,
        long size
) {
}
