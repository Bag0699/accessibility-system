package com.bag.accessibility_system.mappers;

import com.bag.accessibility_system.dtos.request.RegisterRequest;
import com.bag.accessibility_system.dtos.response.AuthResponse;
import com.bag.accessibility_system.entities.User;
import com.bag.accessibility_system.entities.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convierte un RegisterRequest a una entidad User.
     * La contraseña y el rol NO se mapean aquí porque se asignan en el servicio
     * (la contraseña necesita hasheo con BCrypt y el rol viene del endpoint).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(RegisterRequest request);

    /**
     * Convierte una entidad User a un AuthResponse.
     * El token JWT no proviene de la entidad, por eso se ignora aquí
     * y se completa manualmente en el servicio con el método toAuthResponse.
     */
    @Mapping(target = "token", ignore = true)
    AuthResponse toAuthResponse(User user);

    /**
     * Método de conveniencia: construye el AuthResponse completo agregando el token al mapeo base.
     */
    default AuthResponse toAuthResponse(User user, String token) {
        AuthResponse base = toAuthResponse(user);
        return new AuthResponse(base.id(), base.name(), base.email(), base.role(), token);
    }
}
