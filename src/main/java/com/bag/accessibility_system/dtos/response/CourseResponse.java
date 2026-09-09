package com.bag.accessibility_system.dtos.response;

import java.util.UUID;

public record CourseResponse(
        UUID id,
        String name,
        UUID teacherId,
        String teacherName
) {}
