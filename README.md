# Accessibility System API (ClaseAccesible)

Backend de **ClaseAccesible**, una API REST y servidor WebSocket/STOMP construido con **Spring Boot 4** y **Java 21** para gestionar clases en vivo con transcripción de voz en tiempo real, subtítulos accesibles, historial de sesiones y control de asistencia.

## ¿Qué hace este backend?

* **Gestión de usuarios y autenticación:** Registro e inicio de sesión para docentes (`TEACHER`) y estudiantes (`STUDENT`) mediante JWT sin estado (`STATELESS`) y contraseñas cifradas con BCrypt.
* **Cursos y sesiones en vivo:** Permite a los docentes crear cursos, abrir sesiones con código único (formato `XXXX-XXX`) y finalizarlas notificando en vivo a los participantes.
* **Asistencia e historial:** Registra la asistencia de cada estudiante cuando se une a una sesión y restringe la consulta de transcripciones únicamente a estudiantes que asistieron o al docente dueño del curso.
* **Transcripción en tiempo real y persistencia:** Emite tokens efímeros de Deepgram para que el docente transcriba audio desde el navegador sin exponer la API key permanente, recibe los fragmentos por STOMP, los guarda en PostgreSQL y los retransmite al instante a los estudiantes conectados.
* **Conteo de estudiantes conectados en vivo:** Rastrea conexiones WebSocket activas por sesión (`SessionPresenceTracker`) y difunde las actualizaciones de presencia en tiempo real.
* **Preferencias de accesibilidad:** Almacena tamaño de fuente, tema, modo de alto contraste e idioma preferido por usuario.

## Stack tecnológico

* **Lenguaje y Framework:** Java 21, Spring Boot 4.1.1 (WebMVC, Security, Data JPA, WebSocket/STOMP, RestClient, Validation)
* **Base de datos y migraciones:** PostgreSQL + Flyway (`db/migration` + `db/dev-data` en local)
* **Autenticación:** JSON Web Tokens (`jjwt 0.13.0`)
* **Mapeo y utilidades:** MapStruct 1.6.3, Lombok
* **Empaquetado:** Maven Wrapper (`mvnw` / `mvnw.cmd`) y Docker (`Dockerfile` multi-stage con Temurin 21)

---

## Endpoints REST

Todas las rutas tienen prefijo `/api`. Excepto `/api/auth/**`, requieren cabecera `Authorization: Bearer <token>`.

### Autenticación (`/api/auth`) — Públicos

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register/student` | Registra una cuenta con rol `STUDENT` y devuelve JWT (`201`). |
| `POST` | `/api/auth/register/teacher` | Registra una cuenta con rol `TEACHER` y devuelve JWT (`201`). |
| `POST` | `/api/auth/login` | Autentica credenciales y devuelve datos del usuario + JWT (`200`). |

### Cursos (`/api/courses`) — Solo `TEACHER`

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/courses` | Crea un curso asociado al docente autenticado (`201`). |
| `GET` | `/api/courses` | Lista los cursos del docente autenticado. |
| `GET` | `/api/courses/history` | Lista todas las sesiones de los cursos del docente (más recientes primero). |
| `GET` | `/api/courses/{courseId}/history` | Lista las sesiones de un curso específico (validando propiedad). |
| `POST` | `/api/courses/{courseId}/sessions` | Inicia una nueva sesión con código único en el curso (`201`). |

### Sesiones y Transcripciones (`/api/sessions`, `/api/transcription`)

| Método | Ruta | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/sessions/{code}` | Autenticado | Obtiene información de una sesión activa por su código. |
| `POST` | `/api/sessions/{code}/join` | Autenticado | Une al estudiante a la sesión activa y registra su asistencia. |
| `PUT` | `/api/sessions/{code}/end` | Dueño del curso | Finaliza la sesión activa, guarda `endedAt` y notifica por STOMP. |
| `GET` | `/api/sessions/{code}/connected-count` | `TEACHER` (dueño) | Devuelve `{ "connectedCount": N }` de clientes conectados en vivo. |
| `GET` | `/api/sessions/{code}/transcriptions` | Asistente / Dueño | Devuelve los fragmentos de transcripción ordenados por `startTime`. |
| `GET` | `/api/transcription/token` | `TEACHER` | Genera un token efímero de Deepgram para transcribir desde el navegador. |

### Historial del Estudiante (`/api/student`) y Preferencias (`/api/users`)

| Método | Ruta | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/student/history` | Autenticado | Lista las sesiones a las que asistió el estudiante autenticado. |
| `GET` | `/api/student/history/{code}` | Autenticado | Devuelve el detalle de asistencia del estudiante para una sesión. |
| `GET` | `/api/users/me/preferences` | Autenticado | Obtiene las preferencias de accesibilidad del usuario autenticado. |
| `PUT` | `/api/users/me/preferences` | Autenticado | Actualiza tamaño de fuente, tema, alto contraste e idioma preferido. |

---

## WebSocket y STOMP en tiempo real

* **Endpoints de conexión:** `/ws` (WebSocket nativo) y `/ws-sockjs` (fallback con SockJS), habilitados para los orígenes definidos en `APP_CORS_ORIGINS`.
* **Destinos de publicación (`/app`):**
  * `/app/transcription/{sessionCode}`: Recibe `{ text, startTime, endTime }`, persiste el fragmento en PostgreSQL y lo difunde al instante.
* **Tópicos de suscripción (`/topic`):**
  * `/topic/session/{sessionCode}`: Retransmite cada fragmento transcrito guardado (`TranscriptionResponse`).
  * `/topic/session/{sessionCode}/status`: Emite el estado actualizado cuando el docente finaliza la sesión (`isActive: false`).
  * `/topic/session/{sessionCode}/presence`: Emite `{ connectedCount }` en tiempo real cada vez que un cliente se suscribe, desuscribe o desconecta del tópico principal de la sesión.

---

## Base de datos y migraciones

Flyway administra el esquema de PostgreSQL automáticamente al iniciar:

* `db/migration/V1__init_schema.sql`: Usuarios, cursos, sesiones y transcripciones.
* `db/migration/V2__add_session_attendance_and_ended_at.sql`: Tabla de asistencias (`session_attendances`) y fecha de finalización (`ended_at`).
* `db/migration/V3__add_user_preferences.sql`: Preferencias de accesibilidad por usuario (`user_preferences`).
* `db/dev-data/R__dev_seed_data.sql`: Datos de prueba cargados solo fuera del perfil `prod`.

---

## Desarrollo local y pruebas

### Variables de entorno principales (`application.yaml`)

| Variable | Valor por defecto (local) | Descripción |
| :--- | :--- | :--- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/accessibility_db` | URL JDBC de PostgreSQL. |
| `DB_USERNAME` | `postgres` | Usuario de la base de datos. |
| `DB_PASSWORD` | `postgres` | Contraseña de la base de datos. |
| `JWT_SECRET` | *(clave por defecto solo dev)* | Secreto HMAC para firmar tokens JWT. |
| `JWT_EXPIRATION` | `86400000` (24 h) | Expiración del JWT en milisegundos. |
| `APP_CORS_ORIGINS` | `http://localhost:5173` | Orígenes CORS permitidos (separados por comas). |
| `PORT` | `10000` | Puerto HTTP del servidor. |
| `DEEPGRAM_API_KEY` | *(vacío)* | API key de Deepgram para emitir tokens efímeros. |
| `DEEPGRAM_PROJECT_ID` | *(vacío)* | ID del proyecto en Deepgram. |

### Ejecutar localmente

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

### Ejecutar pruebas

```bash
# Pruebas unitarias de seguimiento de presencia WebSocket (no requiere PostgreSQL)
.\mvnw.cmd test -Dtest=SessionPresenceTrackerTest
```

---

## Despliegue en Render con Supabase

1. Crea el proyecto de Supabase. En **Connect**, copia los datos de la conexión **Session pooler** (puerto `5432`). Para JDBC, usa la forma `jdbc:postgresql://HOST:5432/postgres?sslmode=require` en `DB_URL`; copia el usuario en `DB_USERNAME` y la contraseña en `DB_PASSWORD`. No uses la URL HTTPS ni las claves `anon`/`service_role` de la API de Supabase.
2. En Render crea **New > Web Service**, conecta este repositorio y selecciona **Docker** como Language (el `Dockerfile` está en la raíz).
3. En **Environment** configura `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` y `APP_CORS_ORIGINS` (origen exacto del frontend sin barra final; si son varios, sepáralos con comas). Revisa `.env.example` como plantilla.
4. Si se usa transcripción, configura también `DEEPGRAM_API_KEY` y `DEEPGRAM_PROJECT_ID`. El servicio escucha en el `PORT` indicado por Render (`10000` por defecto).
5. Al arrancar con el perfil `prod`, Flyway ejecuta `classpath:db/migration` y excluye `db/dev-data`, por lo que no crea cuentas de prueba.
