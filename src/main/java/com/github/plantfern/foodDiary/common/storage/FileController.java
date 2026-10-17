package com.github.plantfern.foodDiary.common.storage;

import org.springframework.validation.annotation.Validated;


import com.github.plantfern.foodDiary.common.storage.domain.UploadedFileEntity;
import com.github.plantfern.foodDiary.common.storage.exceptionHandlers.FileNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@AllArgsConstructor

@Validated
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;
    private final FileStorageService fileStorageService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FileUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("storage_folder") StorageFolder storageFolder
    ) {

        var entity = fileService.upload(file, storageFolder);

        return ResponseEntity.ok(new FileUploadResponse(
                entity.getId(),
                "/api/files/" + entity.getId(),
                entity.getOriginalName(),
                entity.getContentType(),
                entity.getSize()
        ));
    }

    /**
     * Download by uploaded-file id (numeric) OR by stored relative filename
     * (e.g. FDC_FOOD_00001284.jpg, products/uuid.jpg).
     */
    @GetMapping("/**")
    public ResponseEntity<Resource> download(HttpServletRequest request) {
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatch = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // pattern is /api/files/** — extract remainder after /api/files/
        String key = fullPath;
        if (fullPath != null && fullPath.startsWith("/api/files/")) {
            key = fullPath.substring("/api/files/".length());
        } else if (fullPath != null && fullPath.startsWith("/api/files")) {
            key = fullPath.substring("/api/files".length());
            if (key.startsWith("/")) key = key.substring(1);
        }
        if (key == null || key.isBlank()) {
            throw new FileNotFoundException("File not found.");
        }
        key = URLDecoder.decode(key, StandardCharsets.UTF_8);

        // pure numeric id -> DB entity
        if (key.matches("\\d+")) {
            Long id = Long.parseLong(key);
            var entity = fileService.getEntity(id);
            Resource resource = fileService.load(id);
            MediaType mediaType;
            try {
                mediaType = MediaType.parseMediaType(entity.getContentType());
            } catch (Exception e) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
            return ResponseEntity.ok().contentType(mediaType).body(resource);
        }

        // otherwise treat as stored filename / relative path under storage root
        Resource resource = fileStorageService.loadAsResource(key);
        MediaType mediaType = guessMediaType(key);
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    @GetMapping
    public ResponseEntity<List<FileUploadResponse>> getFiles(
            @RequestParam List<Long> ids
    ) {

        var files = fileService.getEntities(ids);

        return ResponseEntity.ok(
                files
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private FileUploadResponse toResponse(UploadedFileEntity uploadedFileEntity) {

        return new FileUploadResponse(
                uploadedFileEntity.getId(),
                "/api/files/" + uploadedFileEntity.getId(),
                uploadedFileEntity.getOriginalName(),
                uploadedFileEntity.getContentType(),
                uploadedFileEntity.getSize()
        );
    }

    private static MediaType guessMediaType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp")) return MediaType.parseMediaType("image/webp");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
