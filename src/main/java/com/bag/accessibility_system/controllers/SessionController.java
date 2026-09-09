package com.bag.accessibility_system.controllers;

import com.bag.accessibility_system.dtos.response.SessionResponse;
import com.bag.accessibility_system.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    /**
     * POST /api/courses/{courseId}/sessions
     * El docente inicia una nueva sesión dentro de un curso. Protegido por hasRole("TEACHER") en SecurityConfig.
     */
    @PostMapping("/api/courses/{courseId}/sessions")
    public ResponseEntity<SessionResponse> createSession(@PathVariable UUID courseId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.createSession(courseId));
    }

    /**
     * GET /api/sessions/{code}
     * El estudiante valida el código de sesión y obtiene la info para unirse.
     */
    @GetMapping("/api/sessions/{code}")
    public ResponseEntity<SessionResponse> getSessionByCode(@PathVariable String code) {
        return ResponseEntity.ok(sessionService.getActiveSessionByCode(code));
    }

    /**
     * PUT /api/sessions/{code}/end
     * El docente finaliza la sesión activa.
     */
    @PutMapping("/api/sessions/{code}/end")
    public ResponseEntity<SessionResponse> endSession(@PathVariable String code) {
        return ResponseEntity.ok(sessionService.endSession(code));
    }
}
