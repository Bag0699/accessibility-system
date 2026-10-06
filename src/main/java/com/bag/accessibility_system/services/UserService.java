package com.bag.accessibility_system.services;

import com.bag.accessibility_system.dtos.request.UserPreferencesRequest;
import com.bag.accessibility_system.dtos.response.UserPreferencesResponse;

public interface UserService {

    /**
     * Obtiene las preferencias de accesibilidad del usuario autenticado.
     * Si no existen, se crean e inicializan con valores por defecto.
     */
    UserPreferencesResponse getUserPreferences();

    /**
     * Actualiza o crea las preferencias de accesibilidad del usuario autenticado.
     */
    UserPreferencesResponse updateUserPreferences(UserPreferencesRequest request);
}
