package com.bag.accessibility_system.services.impl;

import com.bag.accessibility_system.dtos.request.CreateCourseRequest;
import com.bag.accessibility_system.dtos.response.CourseResponse;
import com.bag.accessibility_system.entities.Course;
import com.bag.accessibility_system.entities.User;
import com.bag.accessibility_system.exceptions.custom.ResourceNotFoundException;
import com.bag.accessibility_system.mappers.CourseMapper;
import com.bag.accessibility_system.repositories.CourseRepository;
import com.bag.accessibility_system.repositories.UserRepository;
import com.bag.accessibility_system.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        User teacher = getAuthenticatedTeacher();

        Course course = courseMapper.toEntity(request);
        course.setTeacher(teacher);

        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesForAuthenticatedTeacher() {
        User teacher = getAuthenticatedTeacher();
        return courseRepository.findAllByTeacher(teacher)
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    // --- Método privado de ayuda: obtiene el usuario autenticado desde el SecurityContext ---

    private User getAuthenticatedTeacher() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario autenticado con el email: " + email
                ));
    }
}
