-- V2__add_session_attendance_and_ended_at.sql
-- Agregar campo ended_at a sessions y crear tabla session_attendances

ALTER TABLE sessions ADD COLUMN ended_at TIMESTAMP WITH TIME ZONE;

CREATE TABLE session_attendances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL,
    student_id UUID NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_attendances_session FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_session_attendances_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_session_attendance_session_student UNIQUE (session_id, student_id)
);

CREATE INDEX idx_session_attendances_session_id ON session_attendances(session_id);
CREATE INDEX idx_session_attendances_student_id ON session_attendances(student_id);
CREATE INDEX idx_session_attendances_joined_at ON session_attendances(joined_at);
