package com.bag.accessibility_system.mappers;

import com.bag.accessibility_system.dtos.response.TranscriptionResponse;
import com.bag.accessibility_system.entities.Transcription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TranscriptionMapper {

    /**
     * Convierte una entidad Transcription a su DTO de respuesta.
     */
    TranscriptionResponse toResponse(Transcription transcription);
}
