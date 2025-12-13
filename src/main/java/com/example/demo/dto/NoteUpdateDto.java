package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteUpdateDto {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String content;

    private boolean completed;
}
