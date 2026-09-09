package com.bag.accessibility_system.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
        String name,

        @NotBlank(message = "El correo es obligatorio.")
        @Email(message = "El formato del correo no es válido.")
        String email,

        @NotBlank(message = "La contraseña es obligatoria.")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
        String password
) {}
