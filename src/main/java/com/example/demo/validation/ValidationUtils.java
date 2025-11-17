package com.example.demo.validation;

import com.example.demo.exception.BadRequestException;

public class ValidationUtils {

    public static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldName + " cannot be blank");
        }
    }

    public static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " cannot be null");
        }
    }
}
