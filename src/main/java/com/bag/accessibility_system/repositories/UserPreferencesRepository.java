package com.bag.accessibility_system.repositories;

import com.bag.accessibility_system.entities.User;
import com.bag.accessibility_system.entities.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {

    Optional<UserPreferences> findByUser(User user);
}
