package com.bag.accessibility_system.services;

import com.bag.accessibility_system.dtos.response.StudentHistoryResponse;

import java.util.List;

public interface StudentService {

    /**
     * Obtiene el historial de sesiones a las que ha asistido el estudiante autenticado.
     */
    List<StudentHistoryResponse> getStudentHistory();
}
