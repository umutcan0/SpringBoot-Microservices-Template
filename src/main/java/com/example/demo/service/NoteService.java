package com.example.demo.service;

//import com.example.demo.exception.NoteNotFoundException;
import com.example.demo.dto.NoteDto;
import com.example.demo.exception.DuplicateNoteTitleException;
import com.example.demo.exception.NoteNotFoundException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @CacheEvict(value = "notes", allEntries = true)
    public void deleteNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));
        noteRepository.delete(note);
    }
    @CacheEvict(value = "noteById", key = "#id")
    public NoteDto updateNote(Long id, NoteDto updatedNoteDto) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        // DTO → Entity
        Note updatedEntity = noteMapper.toEntity(updatedNoteDto);

        existingNote.setTitle(updatedEntity.getTitle());
        existingNote.setContent(updatedEntity.getContent());
        existingNote.setCompleted(updatedEntity.isCompleted());

        Note savedNote = noteRepository.save(existingNote);

        // Entity → DTO
        return noteMapper.toDto(savedNote);
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

    public NoteDto createNote(NoteDto dto) {
        logger.info("Yeni not oluşturuluyor: {}", dto.getTitle());

        try {
            logger.debug("Kaydedilecek not içeriği: {}", dto);
            Note note = noteMapper.toEntity(dto);
            Note saved = noteRepository.save(note);
            logger.info("Not kaydedildi, id: {}", saved.getId());
            return noteMapper.toDto(saved);

        } catch (Exception e) {
            logger.error("Not oluşturma sırasında hata oluştu: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Cacheable(value = "noteById", key = "#id")
    public NoteDto getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", id));
        return noteMapper.toDto(note);
    }
}
