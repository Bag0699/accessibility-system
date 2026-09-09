package com.bag.accessibility_system.dtos.response;

import com.bag.accessibility_system.entities.enums.Role;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String name,
        String email,
        Role role,
        String token
) {}
