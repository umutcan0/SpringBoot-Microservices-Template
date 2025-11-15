package com.example.demo.model;

//import com.example.demo.validation.ValidTitle;
import com.example.demo.dto.NoteDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Date createdAt;
    private boolean completed;

    public Note() {
    }

    public Note(String content, Date createdAt) {
        this.content = content;
        this.createdAt = createdAt;
    }

    public Note(NoteDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.completed = dto.isCompleted();
    }


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    private void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
