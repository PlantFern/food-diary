package com.github.plantfern.foodDiary.common.storage;


import com.github.plantfern.foodDiary.common.storage.domain.UploadedFileEntity;
import com.github.plantfern.foodDiary.common.storage.exceptionHandlers.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class FileService {

    private final FileStorageService fileStorageService;
    private final UploadFileRepository uploadFileRepository;


    @Transactional
    public UploadedFileEntity upload(MultipartFile file) {

        String filename = fileStorageService.store(file);

        try {
            var entity = new UploadedFileEntity(
                    filename,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    LocalDateTime.now()
            );

            return uploadFileRepository.save(entity);
        } catch (Exception e) {
            fileStorageService.delete(filename);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Resource load(Long id){

        var entity = uploadFileRepository
                .findById(id)
                .orElseThrow(
                        () -> new FileNotFoundException("File  not found.")
                );

        return fileStorageService.loadAsResource(entity.getFilename());
    }

    @Transactional(readOnly = true)
    public UploadedFileEntity getEntity(Long id) {

        return uploadFileRepository
                .findById(id)
                .orElseThrow(
                        () -> new FileNotFoundException("File not found.")
                );
    }

    @Transactional
    public void delete(Long id){

        var entity = uploadFileRepository
                .findById(id)
                .orElseThrow(
                        () -> new FileNotFoundException("File  not found.")
                );

        fileStorageService.delete(entity.getFilename());

        uploadFileRepository.delete(entity);
    }
}
