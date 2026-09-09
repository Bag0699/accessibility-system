package com.bag.accessibility_system.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCourseRequest(

        @NotBlank(message = "El nombre del curso es obligatorio.")
        @Size(max = 255, message = "El nombre del curso no puede superar los 255 caracteres.")
        String name
) {}
