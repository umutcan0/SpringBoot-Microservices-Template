package com.example.demo.model;

//import com.example.demo.validation.ValidTitle;
import com.example.demo.dto.NoteDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;


import java.util.Date;

@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false, updatable = false)
    private Date createdAt;
    @Column(nullable = false)
    private Date updatedAt;
    @Column
    private LocalDateTime deletedAt;
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

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;  // ilk oluşturmada eşit olsun
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();   //otomatik güncellenir
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
