package com.bag.accessibility_system.repositories;

import com.bag.accessibility_system.entities.Session;
import com.bag.accessibility_system.entities.Transcription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TranscriptionRepository extends JpaRepository<Transcription, UUID> {

    /**
     * Obtiene todas las transcripciones de una sesión ordenadas cronológicamente por start_time.
     * Usado en GET /api/sessions/{codigo}/transcriptions para el historial completo con tiempos.
     */
    List<Transcription> findAllBySessionOrderByStartTimeAsc(Session session);
}
