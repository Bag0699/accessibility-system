package com.bag.accessibility_system.services;

import com.bag.accessibility_system.dtos.request.CreateCourseRequest;
import com.bag.accessibility_system.dtos.response.CourseResponse;

import java.util.List;

public interface CourseService {

    /**
     * Crea un nuevo curso asociado al docente autenticado en el SecurityContext.
     */
    CourseResponse createCourse(CreateCourseRequest request);

    /**
     * Lista todos los cursos del docente autenticado en el SecurityContext.
     */
    List<CourseResponse> getCoursesForAuthenticatedTeacher();
}
