package com.example.demo.exception;

public class DuplicateNoteTitleException extends RuntimeException {
    public DuplicateNoteTitleException(String title) {
        super("A note with the title '" + title + "' already exists");
    }
}
