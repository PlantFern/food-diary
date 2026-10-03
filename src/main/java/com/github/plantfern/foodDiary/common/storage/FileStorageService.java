package com.github.plantfern.foodDiary.common.storage;

import com.github.plantfern.foodDiary.common.storage.exceptionHandlers.FileNotFoundException;
import com.github.plantfern.foodDiary.common.storage.exceptionHandlers.FileStorageException;
import com.github.plantfern.foodDiary.common.storage.exceptionHandlers.FileTooLargeException;
import com.github.plantfern.foodDiary.common.storage.exceptionHandlers.InvalidFileTypeException;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class FileStorageService {

    private final StorageProperties properties;
    private Path rootLocation;

    public FileStorageService(StorageProperties properties) {
        this.properties = properties;
    }

    public String store(MultipartFile file) {

        if (file.isEmpty())
            throw new FileStorageException("File is empty");

        if (properties.allowTypes() == null
                || !properties.allowTypes().contains(file.getContentType()))
            throw new InvalidFileTypeException("Type " + file.getContentType() + " is not allowed");

        if (file.getSize() > properties.maxSize())
            throw new FileTooLargeException(
                    "Size " + file.getSize() + " is larger than " + properties.maxSize()
            );

        String originName = file.getOriginalFilename();
        String extension = originName != null && originName.contains(".")
                ? originName.substring(originName.lastIndexOf('.'))
                : "";
        String storeName = UUID.randomUUID() + extension;

        Path filePath = rootLocation.resolve(storeName).normalize();

        if (!filePath.startsWith(rootLocation))
            throw new FileStorageException("Cannot access file outside storage dir");

        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + storeName);
        }

        return storeName;
    }

    public Resource loadAsResource(String filename) {

        if (filename == null || filename.isBlank())
            throw new FileNotFoundException("File is empty");

        Path filePath = rootLocation.resolve(filename).normalize();

        if (!filePath.startsWith(rootLocation))
            throw new FileStorageException("Cannot access file outside storage dir");

        if (!Files.exists(filePath))
            throw new FileNotFoundException("File does not exist");

        return new FileSystemResource(filePath);
    }

    public void delete(String filename) {

        if (filename == null || filename.isBlank())
            return;

        Path filePath = rootLocation.resolve(filename).normalize();

        if (!filePath.startsWith(rootLocation))
            throw new FileStorageException("Cannot access file outside storage dir");

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file " + filename);
        }
    }

    @PostConstruct
    public void init() {
        String location = properties.location();
        if (location == null || location.isBlank()) {
            location = "./uploads";
        }
        this.rootLocation = Paths.get(location).toAbsolutePath().normalize();

        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not create directory " + rootLocation);
        }
    }
}
