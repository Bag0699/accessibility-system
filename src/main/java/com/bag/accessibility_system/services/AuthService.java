package com.bag.accessibility_system.services;

import com.bag.accessibility_system.dtos.request.LoginRequest;
import com.bag.accessibility_system.dtos.request.RegisterRequest;
import com.bag.accessibility_system.dtos.response.AuthResponse;

public interface AuthService {

    AuthResponse registerStudent(RegisterRequest request);

    AuthResponse registerTeacher(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
