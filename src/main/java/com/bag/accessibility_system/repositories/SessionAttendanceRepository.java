package com.bag.accessibility_system.repositories;

import com.bag.accessibility_system.entities.Session;
import com.bag.accessibility_system.entities.SessionAttendance;
import com.bag.accessibility_system.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionAttendanceRepository extends JpaRepository<SessionAttendance, UUID> {

    Optional<SessionAttendance> findBySessionAndStudent(Session session, User student);

    boolean existsBySessionAndStudent(Session session, User student);

    @Query("SELECT sa FROM SessionAttendance sa " +
           "JOIN FETCH sa.session s " +
           "JOIN FETCH s.course c " +
           "JOIN FETCH c.teacher t " +
           "WHERE sa.student = :student " +
           "ORDER BY sa.joinedAt DESC")
    List<SessionAttendance> findAllByStudentWithDetailsOrderByJoinedAtDesc(@Param("student") User student);

    @Query("SELECT sa FROM SessionAttendance sa " +
           "JOIN FETCH sa.session s " +
           "JOIN FETCH s.course c " +
           "JOIN FETCH c.teacher t " +
           "WHERE s.code = :code AND sa.student = :student")
    Optional<SessionAttendance> findBySessionCodeAndStudentWithDetails(@Param("code") String code, @Param("student") User student);
}
