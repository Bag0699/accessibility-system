package com.bag.accessibility_system.dtos.response;

public record UserPreferencesResponse(
        String fontSize,
        Boolean highContrast,
        String theme,
        String language
) {}
