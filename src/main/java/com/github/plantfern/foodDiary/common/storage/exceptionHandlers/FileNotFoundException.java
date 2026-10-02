package com.github.plantfern.foodDiary.common.storage.exceptionHandlers;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String message) {
        super(message);
    }
}
