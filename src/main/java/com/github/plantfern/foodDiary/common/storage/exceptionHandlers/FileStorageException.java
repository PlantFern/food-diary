package com.github.plantfern.foodDiary.common.storage.exceptionHandlers;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }
}
