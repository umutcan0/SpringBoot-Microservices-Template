package com.example.demo.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s not found with id: %s", resourceName, resourceId));
    }

    // yardımcı constructor
    public ResourceNotFoundException(Long id) {
        super("Resource not found with id: " + id);
    }
}
