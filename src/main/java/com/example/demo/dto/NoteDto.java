package com.example.demo.dto;

import com.example.demo.model.Note;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoteDto {

    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private boolean completed;

    public NoteDto() {
    }

    public NoteDto(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.content = note.getContent();
        this.completed = note.isCompleted();
    }


}
