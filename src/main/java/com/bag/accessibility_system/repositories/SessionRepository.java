package com.bag.accessibility_system.repositories;

import com.bag.accessibility_system.entities.Course;
import com.bag.accessibility_system.entities.Session;
import com.bag.accessibility_system.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    /**
     * Busca una sesión por su código alfanumérico.
     * Usado cuando el estudiante ingresa el código para unirse (GET /api/sessions/{codigo}).
     */
    Optional<Session> findByCode(String code);

    /**
     * Busca una sesión activa por su código.
     * Útil para validar que la sesión siga en curso antes de permitir el ingreso.
     */
    Optional<Session> findByCodeAndIsActiveTrue(String code);

    /**
     * Lista todas las sesiones de un curso ordenadas por fecha de creación descendente.
     * Usado en GET /api/courses/{curso_id}/history para el historial.
     */
    List<Session> findAllByCourseOrderByCreatedAtDesc(Course course);

    /**
     * Lista todas las sesiones de todos los cursos de un docente ordenadas por fecha descendente.
     * Usado en GET /api/courses/history para el listado global de sesiones recientes del docente.
     */
    List<Session> findAllByCourseTeacherOrderByCreatedAtDesc(User teacher);
}
