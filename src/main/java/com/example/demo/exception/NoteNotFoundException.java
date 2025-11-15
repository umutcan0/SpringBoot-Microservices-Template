package com.example.demo.exception;

public class NoteNotFoundException extends RuntimeException{

    public NoteNotFoundException(Long id) {
        super("Note with id " + id + " not found");
    }
}
