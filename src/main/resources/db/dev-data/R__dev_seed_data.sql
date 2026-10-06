-- R__dev_seed_data.sql
-- Datos mock y semillas de desarrollo para ClaseAccesible
-- Contraseña general para todos los usuarios: 12345678 (hash BCrypt verificado)

-- 1. USUARIOS (2 Docentes, 4 Estudiantes)
INSERT INTO users (id, name, email, password, role) VALUES
  ('a0000000-0000-0000-0000-000000000001', 'Roberto García', 'garcia@gmail.com', '$2a$10$KIXW4Ve.gP/NHVSMT5BuFexn3Q8iK/ZUm.fpYfCdXq9o5ERF5IbuC', 'TEACHER'),
  ('a0000000-0000-0000-0000-000000000002', 'César Ayala', 'ayala@gmail.com', '$2a$10$KIXW4Ve.gP/NHVSMT5BuFexn3Q8iK/ZUm.fpYfCdXq9o5ERF5IbuC', 'STUDENT'),
  ('a0000000-0000-0000-0000-000000000003', 'Elena Mendoza', 'mendoza@universidad.edu', '$2a$10$KIXW4Ve.gP/NHVSMT5BuFexn3Q8iK/ZUm.fpYfCdXq9o5ERF5IbuC', 'TEACHER'),
  ('a0000000-0000-0000-0000-000000000004', 'Sofía Torres', 'sofia.torres@gmail.com', '$2a$10$KIXW4Ve.gP/NHVSMT5BuFexn3Q8iK/ZUm.fpYfCdXq9o5ERF5IbuC', 'STUDENT'),
  ('a0000000-0000-0000-0000-000000000005', 'Lucas Ramírez', 'lucas.ramirez@gmail.com', '$2a$10$KIXW4Ve.gP/NHVSMT5BuFexn3Q8iK/ZUm.fpYfCdXq9o5ERF5IbuC', 'STUDENT'),
  ('a0000000-0000-0000-0000-000000000006', 'Valentina Díaz', 'valentina.diaz@gmail.com', '$2a$10$KIXW4Ve.gP/NHVSMT5BuFexn3Q8iK/ZUm.fpYfCdXq9o5ERF5IbuC', 'STUDENT')
ON CONFLICT (email) DO NOTHING;

-- 2. CURSOS
INSERT INTO courses (id, name, teacher_id) VALUES
  ('b0000000-0000-0000-0000-000000000001', 'Estructuras de Datos y Algoritmos', 'a0000000-0000-0000-0000-000000000001'),
  ('b0000000-0000-0000-0000-000000000002', 'Accesibilidad Web y Diseño Universal', 'a0000000-0000-0000-0000-000000000001'),
  ('b0000000-0000-0000-0000-000000000003', 'Inteligencia Artificial y NLP', 'a0000000-0000-0000-0000-000000000003')
ON CONFLICT (id) DO NOTHING;

-- 3. SESIONES
INSERT INTO sessions (id, code, course_id, created_at, ended_at, is_active) VALUES
  ('c0000000-0000-0000-0000-000000000001', 'EDA-101', 'b0000000-0000-0000-0000-000000000001', '2026-10-04 15:00:00+00', '2026-10-04 16:30:00+00', false),
  ('c0000000-0000-0000-0000-000000000002', 'ACC-202', 'b0000000-0000-0000-0000-000000000002', '2026-10-05 13:30:00+00', '2026-10-05 15:00:00+00', false),
  ('c0000000-0000-0000-0000-000000000003', 'LIVE-99', 'b0000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP - INTERVAL '20 minutes', NULL, true)
ON CONFLICT (code) DO NOTHING;

-- 4. REGISTRO DE ASISTENCIAS (Historial de estudiantes)
INSERT INTO session_attendances (id, session_id, student_id, joined_at) VALUES
  -- Ayala asistió a EDA-101, ACC-202 y a la sesión activa LIVE-99
  ('d0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002', '2026-10-04 15:02:10+00'),
  ('d0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', '2026-10-05 13:31:45+00'),
  ('d0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP - INTERVAL '18 minutes'),
  -- Sofía Torres
  ('d0000000-0000-0000-0000-000000000004', 'c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000004', '2026-10-04 15:03:00+00'),
  ('d0000000-0000-0000-0000-000000000005', 'c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000004', '2026-10-05 13:33:10+00'),
  -- Lucas Ramírez
  ('d0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000005', '2026-10-04 15:05:22+00'),
  -- Valentina Díaz
  ('d0000000-0000-0000-0000-000000000007', 'c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000006', '2026-10-05 13:30:15+00')
ON CONFLICT (session_id, student_id) DO NOTHING;

-- 5. TRANSCRIPCIONES REALISTAS
-- Para la sesión EDA-101 (Árboles Binarios de Búsqueda)
INSERT INTO transcriptions (id, session_id, text, start_time, end_time, created_at) VALUES
  ('e0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'Buenos días a todos. Hoy comenzamos la unidad sobre árboles binarios de búsqueda y grafos dirigidos.', 0.0, 5.2, '2026-10-04 15:00:05+00'),
  ('e0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000001', 'Recuerden que la propiedad fundamental es que los nodos a la izquierda son estrictamente menores que la raíz.', 5.8, 12.5, '2026-10-04 15:00:13+00'),
  ('e0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000001', 'Si realizamos un recorrido en inorden, obtendremos todos los elementos ordenados de forma ascendente.', 13.0, 19.4, '2026-10-04 15:00:20+00'),
  ('e0000000-0000-0000-0000-000000000004', 'c0000000-0000-0000-0000-000000000001', 'La complejidad temporal en el caso promedio para inserción y búsqueda en este árbol es O log n.', 20.0, 27.2, '2026-10-04 15:00:28+00'),
  ('e0000000-0000-0000-0000-000000000005', 'c0000000-0000-0000-0000-000000000001', 'Sin embargo, si los datos entran ordenados, el árbol se desbalancea y se degrada a una lista enlazada O n.', 28.0, 35.8, '2026-10-04 15:00:36+00'),
  ('e0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000001', 'Por esa razón, la próxima semana aprenderemos sobre balanceo automático con árboles AVL y Rojo-Negro.', 36.5, 44.1, '2026-10-04 15:00:45+00'),
  ('e0000000-0000-0000-0000-000000000007', 'c0000000-0000-0000-0000-000000000001', 'Les he subido los ejercicios al repositorio de la clase. Buen trabajo hoy y nos vemos el próximo jueves.', 45.0, 52.0, '2026-10-04 15:00:53+00'),

-- Para la sesión ACC-202 (Accesibilidad Web)
  ('e0000000-0000-0000-0000-000000000008', 'c0000000-0000-0000-0000-000000000002', 'Bienvenidos al módulo de Accesibilidad Web. Hoy analizaremos las pautas WCAG 2.2 nivel AA.', 0.0, 6.2, '2026-10-05 13:30:07+00'),
  ('e0000000-0000-0000-0000-000000000009', 'c0000000-0000-0000-0000-000000000002', 'El ratio de contraste mínimo entre texto y fondo debe ser de 4.5 a 1 para garantizar legibilidad.', 7.0, 14.3, '2026-10-05 13:30:15+00'),
  ('e0000000-0000-0000-0000-000000000010', 'c0000000-0000-0000-0000-000000000002', 'Recuerden que todo elemento interactivo debe ser plenamente operable con teclado usando Tab y Enter.', 15.0, 22.8, '2026-10-05 13:30:23+00')
ON CONFLICT (id) DO NOTHING;

-- 6. PREFERENCIAS DE ACCESIBILIDAD
INSERT INTO user_preferences (id, user_id, font_size, high_contrast, theme, language, updated_at) VALUES
  ('f0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002', 'large', true, 'dark', 'es-ES', CURRENT_TIMESTAMP),
  ('f0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'medium', false, 'light', 'es-ES', CURRENT_TIMESTAMP),
  ('f0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000004', 'medium', false, 'dark', 'es-ES', CURRENT_TIMESTAMP),
  ('f0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000005', 'small', false, 'light', 'es-ES', CURRENT_TIMESTAMP),
  ('f0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000006', 'large', false, 'light', 'es-ES', CURRENT_TIMESTAMP)
ON CONFLICT (user_id) DO NOTHING;
