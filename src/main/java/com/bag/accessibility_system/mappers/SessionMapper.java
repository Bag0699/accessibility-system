package com.bag.accessibility_system.mappers;

import com.bag.accessibility_system.dtos.response.SessionResponse;
import com.bag.accessibility_system.entities.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    /**
     * Convierte una entidad Session a SessionResponse.
     * Mapea los campos anidados del curso (course.id → courseId, course.name → courseName).
     */
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    SessionResponse toResponse(Session session);
}
