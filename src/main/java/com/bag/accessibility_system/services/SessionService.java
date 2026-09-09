package com.bag.accessibility_system.services;

import com.bag.accessibility_system.dtos.response.SessionResponse;

import java.util.UUID;

public interface SessionService {

    /**
     * Crea una nueva sesión dentro de un curso. Solo puede ser invocado por el docente dueño del curso.
     */
    SessionResponse createSession(UUID courseId);

    /**
     * Busca una sesión activa por su código. Usado por el estudiante para unirse a la clase.
     */
    SessionResponse getActiveSessionByCode(String code);

    /**
     * Finaliza una sesión activa. Solo puede ser invocado por el docente dueño del curso.
     */
    SessionResponse endSession(String code);
}
