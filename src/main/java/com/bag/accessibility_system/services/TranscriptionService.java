package com.bag.accessibility_system.services;

import com.bag.accessibility_system.dtos.response.SessionResponse;
import com.bag.accessibility_system.dtos.response.TranscriptionResponse;

import java.util.List;
import java.util.UUID;

public interface TranscriptionService {

    /**
     * Obtiene el historial de sesiones pasadas agrupadas por curso.
     * Solo el docente dueño del curso puede acceder a este historial.
     */
    List<SessionResponse> getCourseHistory(UUID courseId);

    /**
     * Obtiene todas las transcripciones guardadas de una sesión en particular,
     * ordenadas cronológicamente por start_time.
     */
    List<TranscriptionResponse> getSessionTranscriptions(String code);

    /**
     * Guarda un nuevo fragmento de transcripción en la base de datos.
     * Este método será llamado internamente por el listener de WebSockets
     * cada vez que llegue un nuevo texto desde el cliente del docente.
     */
    TranscriptionResponse saveTranscriptionFragment(String sessionCode, String text, Double startTime, Double endTime);
}
