package com.example.demo.controller;

import com.example.demo.dto.NoteCreateDto;
import com.example.demo.dto.NoteDto;
import com.example.demo.dto.NoteResponseDto;
import com.example.demo.dto.NoteUpdateDto;
import com.example.demo.exception.NoteNotFoundException;
import com.example.demo.model.Note;
import com.example.demo.payload.ApiResponse;
import com.example.demo.service.NoteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ApiResponse<List<Note>> getAllNotes() {
        logger.info("Tüm notlar getiriliyor...");
        return noteService.getAllNotes();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponseDto>> createNote(
            @Valid @RequestBody NoteCreateDto noteDto) {

        ApiResponse<NoteResponseDto> response = noteService.createNote(noteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        noteService.deleteNoteById(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDto> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteUpdateDto updatedNoteDto) {

        NoteResponseDto  updatedNote = noteService.updateNote(id, updatedNoteDto);
        return ResponseEntity.ok(updatedNote);
    }

    @GetMapping("/completed")
    public List<Note> getCompletedNotes(){
        return noteService.getCompletedNotes();
    }

    @GetMapping("/sortTheNew")
    public List<Note> sortedNotes() {
        return noteService.sortedTheNewNotes();
    }

    @GetMapping("/paged")
    public ApiResponse<Page<NoteResponseDto>> getNotesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return noteService.getNotesPaged(page, size);
    }


    @GetMapping("/filter")
    public ResponseEntity<List<NoteResponseDto>> filterNotes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false, defaultValue = "asc") String sort) {

        return ResponseEntity.ok(noteService.filterNotes(keyword, completed, sort));
    }

//    @GetMapping("/search")
//    public ResponseEntity<Page<NoteDto>> searchNotes(
//            @RequestParam(required = false) String keyword,
//            @PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
//            Pageable pageable) {
//
//        Page<NoteDto> result = noteService.searchNotes(keyword, pageable);
//        return ResponseEntity.ok(result);
//    }

    @GetMapping("/search")
    public Page<NoteResponseDto> searchNotes(
            @RequestParam String keyword,
            @PageableDefault(
                    page = 0,
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return noteService.searchNotes(keyword,pageable);
    }


    @PutMapping("/{id}/restore")
    public ApiResponse<NoteResponseDto> restore(@PathVariable Long id) {
        return noteService.restoreNote(id);
    }

    // NOTE: This endpoint should be restricted to admin users only
    @DeleteMapping("/{id}/hardDelete")
    public ApiResponse<Void> hardDelete(@PathVariable Long id) {
        return noteService.hardDelete(id);
    }

    /*@ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<String> handleNoteNotFound(NoteNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }*/
}
