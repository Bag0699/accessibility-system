package com.bag.accessibility_system.repositories;

import com.bag.accessibility_system.entities.Course;
import com.bag.accessibility_system.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    /**
     * Lista todos los cursos asociados a un docente.
     * Usado en GET /api/courses para que el docente vea sus cursos.
     */
    List<Course> findAllByTeacher(User teacher);
}
