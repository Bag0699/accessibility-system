package com.bag.accessibility_system.mappers;

import com.bag.accessibility_system.dtos.request.CreateCourseRequest;
import com.bag.accessibility_system.dtos.response.CourseResponse;
import com.bag.accessibility_system.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    /**
     * Convierte un CreateCourseRequest a entidad Course.
     * El id y el docente (teacher) NO se mapean aquí: el teacher se asigna en el
     * servicio a partir del usuario autenticado en el SecurityContext.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Course toEntity(CreateCourseRequest request);

    /**
     * Convierte una entidad Course a CourseResponse.
     * Mapea los campos anidados del docente (teacher.id → teacherId, teacher.name → teacherName).
     */
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.name")
    CourseResponse toResponse(Course course);
}
