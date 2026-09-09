package com.bag.accessibility_system.services.impl;

import com.bag.accessibility_system.dtos.response.SessionResponse;
import com.bag.accessibility_system.dtos.response.TranscriptionResponse;
import com.bag.accessibility_system.entities.Course;
import com.bag.accessibility_system.entities.Session;
import com.bag.accessibility_system.entities.Transcription;
import com.bag.accessibility_system.entities.User;
import com.bag.accessibility_system.exceptions.custom.ResourceNotFoundException;
import com.bag.accessibility_system.mappers.SessionMapper;
import com.bag.accessibility_system.mappers.TranscriptionMapper;
import com.bag.accessibility_system.repositories.CourseRepository;
import com.bag.accessibility_system.repositories.SessionRepository;
import com.bag.accessibility_system.repositories.TranscriptionRepository;
import com.bag.accessibility_system.repositories.UserRepository;
import com.bag.accessibility_system.services.TranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TranscriptionServiceImpl implements TranscriptionService {

    private final TranscriptionRepository transcriptionRepository;
    private final SessionRepository sessionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    
    private final TranscriptionMapper transcriptionMapper;
    private final SessionMapper sessionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> getCourseHistory(UUID courseId) {
        User authenticatedTeacher = getAuthenticatedUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("El curso con id '" + courseId + "' no existe."));

        // Validar que el curso pertenezca al docente autenticado
        if (!course.getTeacher().getId().equals(authenticatedTeacher.getId())) {
            throw new AccessDeniedException("No tienes permiso para ver el historial de este curso.");
        }

        return sessionRepository.findAllByCourseOrderByCreatedAtDesc(course)
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TranscriptionResponse> getSessionTranscriptions(String code) {
        Session session = sessionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("La sesión con código '" + code + "' no existe."));

        // Nota: Las transcripciones de una sesión pueden ser vistas tanto por el docente
        // como por los estudiantes que tengan el código, por lo que no aplicamos restricción
        // estricta de propiedad de curso aquí, a menos que el requerimiento cambie.
        
        return transcriptionRepository.findAllBySessionOrderByStartTimeAsc(session)
                .stream()
                .map(transcriptionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TranscriptionResponse saveTranscriptionFragment(String sessionCode, String text, Double startTime, Double endTime) {
        Session session = sessionRepository.findByCode(sessionCode)
                .orElseThrow(() -> new ResourceNotFoundException("La sesión con código '" + sessionCode + "' no existe."));

        // Si se desea, se podría validar que la sesión esté activa (session.getIsActive())
        // antes de guardar, dependiendo de las reglas de negocio estrictas.
        
        Transcription transcription = new Transcription();
        transcription.setSession(session);
        transcription.setText(text);
        transcription.setStartTime(startTime);
        transcription.setEndTime(endTime);

        Transcription saved = transcriptionRepository.save(transcription);
        return transcriptionMapper.toResponse(saved);
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario autenticado con el email: " + email
                ));
    }
}
