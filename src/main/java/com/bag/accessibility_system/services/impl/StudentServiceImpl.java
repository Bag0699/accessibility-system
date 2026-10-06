package com.bag.accessibility_system.services.impl;

import com.bag.accessibility_system.dtos.response.StudentHistoryResponse;
import com.bag.accessibility_system.entities.SessionAttendance;
import com.bag.accessibility_system.entities.User;
import com.bag.accessibility_system.exceptions.custom.ResourceNotFoundException;
import com.bag.accessibility_system.repositories.SessionAttendanceRepository;
import com.bag.accessibility_system.repositories.UserRepository;
import com.bag.accessibility_system.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final SessionAttendanceRepository sessionAttendanceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StudentHistoryResponse> getStudentHistory() {
        User student = getAuthenticatedUser();
        List<SessionAttendance> attendances = sessionAttendanceRepository
                .findAllByStudentWithDetailsOrderByJoinedAtDesc(student);

        return attendances.stream()
                .map(att -> new StudentHistoryResponse(
                        att.getSession().getId(),
                        att.getSession().getCode(),
                        att.getSession().getCourse().getName(),
                        att.getSession().getCourse().getTeacher().getName(),
                        att.getJoinedAt(),
                        att.getSession().getEndedAt()
                ))
                .toList();
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario autenticado con el email: " + email
                ));
    }
}
