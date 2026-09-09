package com.bag.accessibility_system.services.impl;

import com.bag.accessibility_system.dtos.request.LoginRequest;
import com.bag.accessibility_system.dtos.request.RegisterRequest;
import com.bag.accessibility_system.dtos.response.AuthResponse;
import com.bag.accessibility_system.entities.User;
import com.bag.accessibility_system.entities.enums.Role;
import com.bag.accessibility_system.exceptions.custom.DuplicateResourceException;
import com.bag.accessibility_system.mappers.UserMapper;
import com.bag.accessibility_system.repositories.UserRepository;
import com.bag.accessibility_system.security.JwtService;
import com.bag.accessibility_system.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse registerStudent(RegisterRequest request) {
        return register(request, Role.STUDENT);
    }

    @Override
    @Transactional
    public AuthResponse registerTeacher(RegisterRequest request) {
        return register(request, Role.TEACHER);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Spring Security válida las credenciales y lanza BadCredentialsException si son incorrectas,
        // la cual es capturada por el GlobalExceptionHandler.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);
        return userMapper.toAuthResponse(user, token);
    }

    private AuthResponse register(RegisterRequest request, Role role) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Ya existe un usuario registrado con el correo: " + request.email()
            );
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(savedUser.getEmail())
                .password(savedUser.getPassword())
                .roles(savedUser.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);
        return userMapper.toAuthResponse(savedUser, token);
    }
}
