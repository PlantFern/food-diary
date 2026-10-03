package com.github.plantfern.foodDiary.common.storage;


import com.github.plantfern.foodDiary.common.storage.domain.UploadedFileEntity;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@AllArgsConstructor

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FileUploadResponse> upload(
            @RequestParam("file") MultipartFile file
    ) {

        var entity = fileService.upload(file);

        return ResponseEntity.ok(new FileUploadResponse(
                entity.getId(),
                "/api/files/" + entity.getId(),
                entity.getOriginalName(),
                entity.getContentType(),
                entity.getSize()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {

        var entity = fileService.getEntity(id);
        Resource resource = fileService.load(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(entity.getContentType()))
                .body(resource);
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
}
