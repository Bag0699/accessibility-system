package com.bag.accessibility_system.controllers;

import com.bag.accessibility_system.dtos.response.SessionResponse;
import com.bag.accessibility_system.dtos.response.TranscriptionResponse;
import com.bag.accessibility_system.services.TranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;

    /**
     * GET /api/courses/history
     * Obtiene la lista de todas las sesiones de todos los cursos del docente autenticado,
     * ordenadas de la más reciente a la más antigua.
     * Protegido por hasRole("TEACHER") a nivel de configuración (/api/courses/**).
     */
    @GetMapping("/courses/history")
    public ResponseEntity<List<SessionResponse>> getAllTeacherSessions() {
        return ResponseEntity.ok(transcriptionService.getAllTeacherSessions());
    }

    /**
     * GET /api/courses/{courseId}/history
     * Obtiene la lista de sesiones de un curso específico, ordenadas de más reciente a más antigua.
     * Protegido por hasRole("TEACHER") a nivel de configuración (/api/courses/**).
     */
    @GetMapping("/courses/{courseId}/history")
    public ResponseEntity<List<SessionResponse>> getCourseHistory(@PathVariable UUID courseId) {
        return ResponseEntity.ok(transcriptionService.getCourseHistory(courseId));
    }

    /**
     * GET /api/sessions/{code}/transcriptions
     * Obtiene todo el texto transcrito de una sesión en particular.
     * Accesible tanto para docentes como para alumnos autenticados.
     */
    @GetMapping("/sessions/{code}/transcriptions")
    public ResponseEntity<List<TranscriptionResponse>> getSessionTranscriptions(@PathVariable String code) {
        return ResponseEntity.ok(transcriptionService.getSessionTranscriptions(code));
    }
}
