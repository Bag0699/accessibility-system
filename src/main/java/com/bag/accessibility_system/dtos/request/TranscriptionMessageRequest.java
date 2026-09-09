package com.bag.accessibility_system.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TranscriptionMessageRequest(

        @NotBlank(message = "El texto de la transcripción no puede estar vacío.")
        String text,

        @NotNull(message = "El tiempo de inicio es obligatorio.")
        Double startTime,

        @NotNull(message = "El tiempo de fin es obligatorio.")
        Double endTime
) {}
