package com.example.demo.service;

//import com.example.demo.exception.NoteNotFoundException;
import com.example.demo.dto.NoteDto;
import com.example.demo.exception.DuplicateNoteTitleException;
import com.example.demo.exception.NoteNotFoundException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Note;
import com.example.demo.payload.ApiResponse;
import com.example.demo.repository.NoteRepository;
import com.example.demo.validation.ValidationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.mapper.NoteMapper;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class NoteService {
    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;

    }

    @Cacheable("notes")
    public ApiResponse<List<Note>> getAllNotes() {
        List<Note> notes = noteRepository.findAllByDeletedAtIsNull();
        return ApiResponse.success(notes, "Notes listed successfully");
    }

    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public ApiResponse<Void> deleteNoteById(Long id) {

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        note.setDeletedAt(LocalDateTime.now());
        noteRepository.save(note);

        return ApiResponse.success(null, "Note deleted successfully (soft delete)");
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "noteById", key = "#id"),
            @CacheEvict(value = "notes", allEntries = true)
    })
    public NoteDto updateNote(Long id, NoteDto dto) {

        if (dto.getTitle().equals("err")) {
            throw new RuntimeException("test rollback");
        }

        Note existing = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setCompleted(dto.isCompleted());

        Note saved = noteRepository.save(existing);
        return noteMapper.toDto(saved);
    }


    public List<Note> getCompletedNotes() {
        return noteRepository.findByCompletedTrue();
    }

    public List<Note> searchNotesByTitle(String title) {
        return noteRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Note> sortedTheNewNotes() {
        return noteRepository.findAllByOrderByCreatedAtDesc();
    }

    public Page<NoteDto> getPagedNotes(int page, int size, String[] sort) {
        // sort parametresini ayır
        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "asc";

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        return noteRepository.findAll(pageable)
                .map(noteMapper::toDto);
    }

    public List<NoteDto> filterNotes(String keyword, Boolean completed, String sortDir) {
        Sort sort = sortDir != null && sortDir.equalsIgnoreCase("desc") ?
                Sort.by("createdAt").descending() :
                Sort.by("createdAt").ascending();

        return noteRepository.filterNotes(keyword, completed, sort)
                .stream()
                .map(noteMapper::toDto)
                .toList();
    }

    public Page<NoteDto> searchNotes(String keyword, Boolean completed, Pageable pageable) {
        return noteRepository.searchNotes(keyword, completed, pageable)
                .map(noteMapper::toDto);
    }

    public ApiResponse<NoteDto> createNote(NoteDto dto) {

        ValidationUtils.requireNotBlank(dto.getTitle(), "title");
        ValidationUtils.requireNotBlank(dto.getContent(), "content");

        Note note = noteMapper.toEntity(dto);
        noteRepository.save(note);

        return ApiResponse.success(null, "Note deleted successfully");
    }

    @Cacheable(value = "noteById", key = "#id")
    public NoteDto getNoteById(Long id) {

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        return noteMapper.toDto(note);
    }
    @Transactional
    public ApiResponse<NoteDto> restoreNote(Long id) {
        Note note = noteRepository.findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deleted Note", id));

        note.setDeletedAt(null); // geri getirdik
        Note saved = noteRepository.save(note);

        return ApiResponse.success(noteMapper.toDto(saved), "Note restored successfully");    }


}
