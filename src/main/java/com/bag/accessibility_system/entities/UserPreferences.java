package com.bag.accessibility_system.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "font_size", nullable = false, length = 20)
    private String fontSize;

    @Column(name = "high_contrast", nullable = false)
    private Boolean highContrast;

    @Column(name = "theme", nullable = false, length = 20)
    private String theme;

    @Column(name = "language", nullable = false, length = 10)
    private String language;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.fontSize == null) this.fontSize = "medium";
        if (this.highContrast == null) this.highContrast = false;
        if (this.theme == null) this.theme = "light";
        if (this.language == null) this.language = "es-ES";
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
