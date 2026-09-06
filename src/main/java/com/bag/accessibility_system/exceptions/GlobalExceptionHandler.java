package com.bag.accessibility_system.exceptions;

import com.bag.accessibility_system.exceptions.custom.BadRequestException;
import com.bag.accessibility_system.exceptions.custom.DuplicateResourceException;
import com.bag.accessibility_system.exceptions.custom.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Errores de validación de DTO (@Valid).
     * Extrae los errores campo por campo para que el frontend pueda mostrarlos junto al input correspondiente.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Valor inválido",
                        (existing, duplicate) -> existing // en caso de múltiples errores en el mismo campo, conservar el primero
                ));

        log.warn("Errores de validación en {}: {}", request.getRequestURI(), fieldErrors);

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "La solicitud contiene campos con datos inválidos.",
                request.getRequestURI(),
                LocalDateTime.now(),
                fieldErrors
        );
    }

    /**
     * Recurso no encontrado (sesión, curso, usuario).
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado en {}: {}", request.getRequestURI(), ex.getMessage());

        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Recurso duplicado (ej. email ya registrado).
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateResourceException.class)
    public ErrorResponse handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("Conflicto de recurso en {}: {}", request.getRequestURI(), ex.getMessage());

        return new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Solicitud incorrecta por lógica de negocio (ej. sesión ya finalizada, código inválido).
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        log.warn("Bad request en {}: {}", request.getRequestURI(), ex.getMessage());

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Credenciales incorrectas en el login.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Credenciales incorrectas en {}", request.getRequestURI());

        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Correo o contraseña incorrectos.",
                request.getRequestURI()
        );
    }

    /**
     * Acceso denegado (ej. un estudiante intenta usar un endpoint exclusivo para docentes).
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acceso denegado en {}", request.getRequestURI());

        return new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "No tienes permisos para realizar esta acción.",
                request.getRequestURI()
        );
    }

    /**
     * Fallback: cualquier error no controlado.
     * Se loguea la traza completa internamente, pero se devuelve un mensaje genérico al cliente.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {}: ", request.getRequestURI(), ex);

        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error interno en el servidor. Por favor, inténtalo de nuevo más tarde.",
                request.getRequestURI()
        );
    }
}
