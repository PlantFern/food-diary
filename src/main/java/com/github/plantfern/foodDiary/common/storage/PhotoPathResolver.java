package com.github.plantfern.foodDiary.common.storage;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class PhotoPathResolver {

    private final FileService fileService;

    public String fromFileId(Long fileId) {

        if(fileId == null) return null;

        fileService.getEntity(fileId);
        return "/api/files/" + fileId;
    }

    public String fromPathOrUrl(String value) {

        if(value == null || value.isBlank()) return null;

        if(value.startsWith("/http://") || value.startsWith("/https://"))
            return value;

        if(value.startsWith("/api/files/")) {
            Long id = Long.parseLong(value.substring("/api/files/".length()));
            fileService.getEntity(id);
            return "/api/files/" + id;
        }
        throw new IllegalArgumentException("Invalid path or url value");
    }
}
