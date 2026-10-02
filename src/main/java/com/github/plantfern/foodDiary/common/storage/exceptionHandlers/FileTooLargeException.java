package com.github.plantfern.foodDiary.common.storage.exceptionHandlers;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException(String message) {
        super(message);
    }
}
