package com.bag.accessibility_system.repositories;

import com.bag.accessibility_system.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca un usuario por su email. Usado en el login y para verificar duplicados en el registro.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario con ese email. Usado en el registro para lanzar DuplicateResourceException.
     */
    boolean existsByEmail(String email);
}
