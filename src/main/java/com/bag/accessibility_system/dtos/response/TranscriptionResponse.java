package com.bag.accessibility_system.dtos.response;

import java.util.UUID;

public record TranscriptionResponse(
        UUID id,
        String text,
        Double startTime,
        Double endTime
) {}
