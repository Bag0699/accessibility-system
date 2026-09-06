-- V1__init_schema.sql
-- Inicialización del esquema de base de datos para el Sistema de Accesibilidad

-- Habilitar extensión pgcrypto para gen_random_uuid() si la versión de Postgres lo requiere
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. Tabla: users
-- Se utiliza 'users' para evitar colisiones con la palabra reservada 'user' en PostgreSQL
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT chk_user_role CHECK (role IN ('TEACHER', 'STUDENT'))
);

-- 2. Tabla: courses
CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    teacher_id UUID NOT NULL,
    CONSTRAINT fk_courses_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Tabla: sessions
CREATE TABLE sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(10) NOT NULL UNIQUE,
    course_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_sessions_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 4. Tabla: transcriptions
CREATE TABLE transcriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL,
    text TEXT NOT NULL,
    start_time DOUBLE PRECISION,
    end_time DOUBLE PRECISION,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transcriptions_session FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento de consultas frecuentes y llaves foráneas
CREATE INDEX idx_courses_teacher_id ON courses(teacher_id);
CREATE INDEX idx_sessions_code ON sessions(code);
CREATE INDEX idx_sessions_course_id ON sessions(course_id);
CREATE INDEX idx_transcriptions_session_id ON transcriptions(session_id);
CREATE INDEX idx_transcriptions_session_start_time ON transcriptions(session_id, start_time);
