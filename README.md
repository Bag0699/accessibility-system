# Accessibility System API

Backend Spring Boot 4 con Java 21, PostgreSQL y Flyway.

## Despliegue en Render con Supabase

1. Crea el proyecto de Supabase. En **Connect**, copia los datos de la conexión **Session pooler** (puerto `5432`). Para JDBC, usa la forma `jdbc:postgresql://HOST:5432/postgres?sslmode=require` en `DB_URL`; copia el usuario del mismo modo de conexión en `DB_USERNAME` y la contraseña de la base de datos en `DB_PASSWORD`. No uses la URL HTTPS ni las claves `anon`/`service_role` de la API de Supabase.
2. Sube este repositorio a GitHub. En Render crea **New > Web Service**, conecta el repositorio y selecciona **Docker** como Language. El `Dockerfile` está en la raíz del repositorio.
3. En **Environment** configura `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` y `APP_CORS_ORIGINS`. Usa el origen exacto del frontend, por ejemplo `https://mi-frontend.onrender.com`, sin barra final. Si hay varios orígenes, sepáralos con comas. `JWT_SECRET` debe ser una clave aleatoria larga y estable entre despliegues.
4. Si se usa transcripción, configura también `DEEPGRAM_API_KEY` y `DEEPGRAM_PROJECT_ID`. El servicio escucha el `PORT` que indique Render, con `10000` como valor predeterminado.
5. Despliega y revisa los logs. Flyway crea las tablas de la aplicación en Supabase al arrancar. El perfil `prod` excluye `db/dev-data`, por lo que no crea cuentas de prueba.

Las variables reales se guardan en Render, no en Git. El archivo `.env.example` contiene solo marcadores de posición. Esta aplicación usa sus propias tablas y autenticación JWT; no requiere Supabase Auth.

## Desarrollo local

Sin el perfil `prod`, la aplicación usa PostgreSQL local por defecto y carga los datos de desarrollo de Flyway. Ejecuta `./mvnw spring-boot:run` en macOS/Linux o `./mvnw.cmd spring-boot:run` en Windows.

Para ejecutar pruebas unitarias:

```bash
./mvnw.cmd test -Dtest=SessionPresenceTrackerTest
```

## Capacidades principales

* **Autenticación y roles (`/api/auth/**`):** Registro e inicio de sesión para `TEACHER` y `STUDENT` mediante JWT y contraseñas cifradas con BCrypt.
* **Cursos y sesiones (`/api/courses/**`, `/api/sessions/**`):** Creación de cursos y sesiones con código único, unión con registro de asistencia (`SessionAttendance`), cierre de sesión con notificación en tiempo real y consulta de estudiantes conectados en vivo (`GET /api/sessions/{code}/connected-count`).
* **Transcripción y tokens efímeros (`/api/transcription/token`, `/api/sessions/{code}/transcriptions`):** Emisión de credenciales efímeras de Deepgram para docentes, persistencia de fragmentos y consulta protegida por rol y asistencia.
* **Historial y preferencias (`/api/courses/history`, `/api/courses/{courseId}/history`, `/api/student/history/**`, `/api/users/me/preferences`):** Consulta de sesiones para docentes y estudiantes, y persistencia de preferencias de accesibilidad.
* **Tiempo real STOMP (`/ws`, `/ws-sockjs`):**
  * `/app/transcription/{sessionCode}` → `/topic/session/{sessionCode}` para fragmentos de transcripción.
  * `/topic/session/{sessionCode}/status` para notificar la finalización de la sesión.
  * `/topic/session/{sessionCode}/presence` para difundir en vivo el conteo de estudiantes conectados (`SessionPresenceTracker`).
