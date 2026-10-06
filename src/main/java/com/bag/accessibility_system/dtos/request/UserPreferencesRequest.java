package com.bag.accessibility_system.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserPreferencesRequest(
        @NotBlank(message = "El tamaño de fuente es obligatorio")
        String fontSize,

        @NotNull(message = "El alto contraste es obligatorio")
        Boolean highContrast,

        @NotBlank(message = "El tema es obligatorio")
        String theme,

        @NotBlank(message = "El idioma es obligatorio")
        String language
) {}
