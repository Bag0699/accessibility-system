package com.bag.accessibility_system.dtos.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionResponse(
        UUID id,
        String code,
        UUID courseId,
        String courseName,
        OffsetDateTime createdAt,
        Boolean isActive
) {}
