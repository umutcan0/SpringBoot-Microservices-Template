package com.example.demo.service;

//import com.example.demo.exception.NoteNotFoundException;
import com.example.demo.dto.NoteCreateDto;
import com.example.demo.dto.NoteDto;
import com.example.demo.dto.NoteResponseDto;
import com.example.demo.dto.NoteUpdateDto;
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
import java.util.Optional;

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
    public NoteResponseDto updateNote(Long id, NoteUpdateDto dto) {

        Note existing = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found", id));

        noteMapper.updateEntityFromDto(dto, existing);
        Note saved = noteRepository.save(existing);

        return noteMapper.toResponseDto(saved);
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

    @Cacheable(value = "notesPaged", key = "#page + '-' + #size")
    public ApiResponse<Page<NoteResponseDto>> getNotesPaged(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Note> notes = noteRepository.findAllByDeletedAtIsNull(pageable);

        Page<NoteResponseDto> dtoPage = notes.map(noteMapper::toResponseDto);

        return ApiResponse.success(dtoPage, "Notes listed with pagination");
    }


    public List<NoteResponseDto> filterNotes(String keyword, Boolean completed, String sortDir) {
        Sort sort = sortDir != null && sortDir.equalsIgnoreCase("desc") ?
                Sort.by("createdAt").descending() :
                Sort.by("createdAt").ascending();

        return noteRepository.filterNotes(keyword, completed, sort)
                .stream()
                .map(noteMapper::toResponseDto)
                .toList();
    }

    public Page<NoteResponseDto> searchNotes(String keyword, Pageable pageable) {
        Page<Note> page = noteRepository.searchNotes(keyword, pageable);

        return page.map(noteMapper::toResponseDto);
    }

    public ApiResponse<NoteResponseDto> createNote(NoteCreateDto dto) {

        ValidationUtils.requireNotBlank(dto.getTitle(), "title");
        ValidationUtils.requireNotBlank(dto.getContent(), "content");

        Note note = noteMapper.toEntity(dto);
        Note savedNote = noteRepository.save(note);
        NoteResponseDto responseDto = noteMapper.toResponseDto(savedNote);

        return ApiResponse.success(responseDto, "Note created successfully");
    }

    @Cacheable(value = "noteById", key = "#id")
    public NoteResponseDto getNoteById(Long id) {

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        return noteMapper.toResponseDto(note);
    }
    @Transactional
    public ApiResponse<NoteResponseDto> restoreNote(Long id) {
        Note note = noteRepository.findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deleted Note", id));

        note.setDeletedAt(null); // geri getirdik
        Note saved = noteRepository.save(note);

        return ApiResponse.success(noteMapper.toResponseDto(saved), "Note restored successfully");    }

    @Transactional
    public  ApiResponse<Void> hardDelete(Long id){
        Note note = noteRepository.findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Note Found", id));
        noteRepository.delete(note);

        return ApiResponse.success(null, "Note deleted successfully (hard delete)");

    }


}
