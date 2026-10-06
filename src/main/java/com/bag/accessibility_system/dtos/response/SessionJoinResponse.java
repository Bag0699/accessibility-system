package com.bag.accessibility_system.dtos.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionJoinResponse(
        UUID id,
        String code,
        String courseName,
        String teacherName,
        Boolean isActive,
        OffsetDateTime startedAt
) {}
