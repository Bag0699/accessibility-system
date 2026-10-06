package com.bag.accessibility_system.services.impl;

import com.bag.accessibility_system.dtos.request.UserPreferencesRequest;
import com.bag.accessibility_system.dtos.response.UserPreferencesResponse;
import com.bag.accessibility_system.entities.User;
import com.bag.accessibility_system.entities.UserPreferences;
import com.bag.accessibility_system.exceptions.custom.ResourceNotFoundException;
import com.bag.accessibility_system.repositories.UserPreferencesRepository;
import com.bag.accessibility_system.repositories.UserRepository;
import com.bag.accessibility_system.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserPreferencesResponse getUserPreferences() {
        User user = getAuthenticatedUser();

        UserPreferences preferences = userPreferencesRepository.findByUser(user)
                .orElseGet(() -> {
                    UserPreferences defaultPreferences = new UserPreferences();
                    defaultPreferences.setUser(user);
                    defaultPreferences.setFontSize("medium");
                    defaultPreferences.setHighContrast(false);
                    defaultPreferences.setTheme("light");
                    defaultPreferences.setLanguage("es-ES");
                    return userPreferencesRepository.save(defaultPreferences);
                });

        return toResponse(preferences);
    }

    @Override
    @Transactional
    public UserPreferencesResponse updateUserPreferences(UserPreferencesRequest request) {
        User user = getAuthenticatedUser();

        UserPreferences preferences = userPreferencesRepository.findByUser(user)
                .orElseGet(() -> {
                    UserPreferences newPreferences = new UserPreferences();
                    newPreferences.setUser(user);
                    return newPreferences;
                });

        preferences.setFontSize(request.fontSize());
        preferences.setHighContrast(request.highContrast());
        preferences.setTheme(request.theme());
        preferences.setLanguage(request.language());

        UserPreferences saved = userPreferencesRepository.save(preferences);
        return toResponse(saved);
    }

    private UserPreferencesResponse toResponse(UserPreferences entity) {
        return new UserPreferencesResponse(
                entity.getFontSize(),
                entity.getHighContrast(),
                entity.getTheme(),
                entity.getLanguage()
        );
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario autenticado con el email: " + email
                ));
    }
}
