package com.example.demo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NoteResponseDto {

    private Long id;
    private String title;
    private String content;
    private boolean completed;
    private Date createdAt;
    private Date updatedAt;
}
