package com.bag.accessibility_system.dtos.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StudentHistoryResponse(
        UUID sessionId,
        String code,
        String courseName,
        String teacherName,
        OffsetDateTime joinedAt,
        OffsetDateTime endedAt
) {}
