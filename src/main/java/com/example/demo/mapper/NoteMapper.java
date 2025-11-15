package com.example.demo.mapper;
import com.example.demo.model.Note;
import com.example.demo.dto.NoteDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    NoteDto toDto(Note note);
    Note toEntity(NoteDto dto);
}
