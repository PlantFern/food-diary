package com.github.plantfern.foodDiary.common.storage.exceptionHandlers;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
