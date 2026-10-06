package com.bag.accessibility_system.controllers;

import com.bag.accessibility_system.dtos.request.UserPreferencesRequest;
import com.bag.accessibility_system.dtos.response.UserPreferencesResponse;
import com.bag.accessibility_system.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me/preferences
     * Obtiene las preferencias de accesibilidad del usuario autenticado.
     */
    @GetMapping("/me/preferences")
    public ResponseEntity<UserPreferencesResponse> getPreferences() {
        return ResponseEntity.ok(userService.getUserPreferences());
    }

    /**
     * PUT /api/users/me/preferences
     * Actualiza o guarda las preferencias de accesibilidad del usuario autenticado.
     */
    @PutMapping("/me/preferences")
    public ResponseEntity<UserPreferencesResponse> updatePreferences(@Valid @RequestBody UserPreferencesRequest request) {
        return ResponseEntity.ok(userService.updateUserPreferences(request));
    }
}
