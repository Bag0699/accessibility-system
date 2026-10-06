package com.bag.accessibility_system.controllers;

import com.bag.accessibility_system.dtos.response.StudentHistoryResponse;
import com.bag.accessibility_system.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * GET /api/student/history
     * Retorna la lista de sesiones a las que el estudiante autenticado se ha unido.
     */
    @GetMapping("/history")
    public ResponseEntity<List<StudentHistoryResponse>> getHistory() {
        return ResponseEntity.ok(studentService.getStudentHistory());
    }

    /**
     * GET /api/student/history/{code}
     * Retorna los datos de una sesión asistida por el estudiante según su código.
     */
    @GetMapping("/history/{code}")
    public ResponseEntity<StudentHistoryResponse> getSessionDetails(@PathVariable String code) {
        return ResponseEntity.ok(studentService.getStudentSessionByCode(code));
    }
}
