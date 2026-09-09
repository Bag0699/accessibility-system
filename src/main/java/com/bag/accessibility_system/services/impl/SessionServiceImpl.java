package com.bag.accessibility_system.services.impl;

import com.bag.accessibility_system.dtos.response.SessionResponse;
import com.bag.accessibility_system.entities.Course;
import com.bag.accessibility_system.entities.Session;
import com.bag.accessibility_system.entities.User;
import com.bag.accessibility_system.exceptions.custom.BadRequestException;
import com.bag.accessibility_system.exceptions.custom.ResourceNotFoundException;
import com.bag.accessibility_system.mappers.SessionMapper;
import com.bag.accessibility_system.repositories.CourseRepository;
import com.bag.accessibility_system.repositories.SessionRepository;
import com.bag.accessibility_system.repositories.UserRepository;
import com.bag.accessibility_system.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private static final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SessionRepository sessionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final SessionMapper sessionMapper;

    @Override
    @Transactional
    public SessionResponse createSession(UUID courseId) {
        User authenticatedUser = getAuthenticatedUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El curso con id '" + courseId + "' no existe."
                ));

        // Válida que el docente autenticado sea el dueño del curso
        if (!course.getTeacher().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException(
                    "No tienes permiso para crear sesiones en este curso."
            );
        }

        Session session = new Session();
        session.setCode(generateUniqueCode());
        session.setCourse(course);
        // createdAt e isActive se asignan automáticamente en @PrePersist

        Session savedSession = sessionRepository.save(session);
        return sessionMapper.toResponse(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponse getActiveSessionByCode(String code) {
        Session session = sessionRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La sesión con código '" + code + "' no existe o ya ha finalizado."
                ));
        return sessionMapper.toResponse(session);
    }

    @Override
    @Transactional
    public SessionResponse endSession(String code) {
        User authenticatedUser = getAuthenticatedUser();

        Session session = sessionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La sesión con código '" + code + "' no existe."
                ));

        // Válida que el docente autenticado sea el dueño del curso al que pertenece esta sesión
        if (!session.getCourse().getTeacher().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException(
                    "No tienes permiso para finalizar esta sesión."
            );
        }

        if (!session.getIsActive()) {
            throw new BadRequestException(
                    "La sesión con código '" + code + "' ya ha sido finalizada."
            );
        }

        session.setIsActive(false);
        return sessionMapper.toResponse(sessionRepository.save(session));
    }

    // --- Métodos privados de ayuda ---

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario autenticado con el email: " + email
                ));
    }

    /**
     * Genera un código alfanumérico único de 7 caracteres en formato XXXXX-XX (ej. A7BXC-9X).
     * Reintenta si el código ya existe en la base de datos (colisión extremadamente improbable).
     */
    private String generateUniqueCode() {
        String code;
        do {
            code = generateCode();
        } while (sessionRepository.findByCode(code).isPresent());
        return code;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARACTERS.charAt(RANDOM.nextInt(CODE_CHARACTERS.length())));
        }
        // Inserta un guion en la posición 4 para el formato: XXXX-XXX (ej. A7BX-9X2)
        sb.insert(4, '-');
        return sb.toString();
    }
}
