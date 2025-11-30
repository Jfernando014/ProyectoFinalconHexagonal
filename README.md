# Gestión del Proceso de Trabajo de Grado — Microservicios (Segundo Corte)

Sistema para gestionar el flujo de trabajos de grado en la FIET (Universidad del Cauca). Este repositorio corresponde a la **segunda entrega**, donde la solución evoluciona a **arquitectura de microservicios** con comunicación **REST** y **eventos** en RabbitMQ.

> Objetivo: registrar usuarios, gestionar envío y evaluación del **Formato A**, manejo de documentos PDF, notificaciones y trazabilidad del estado del proyecto.

---

## 🔎 Vista rápida

* **Arquitectura:** Microservicios + mensajería (RabbitMQ)
* **Servicios:** users, projects, documents, messaging, notifications
* **Patrones:** State, Strategy, Factory Method, Observer (EDA), Facade, DTO, Repository
* **Pruebas:** unitarias por servicio (servicios y dominio)
* **Docs:** Swagger por servicio y modelos C4/UML

---

## 🧱 Arquitectura

**Desacoplamiento por bounded context** y **Database per Service**. Comunicación síncrona por **REST** y asíncrona por **eventos** con RabbitMQ. Los servicios son **stateless** para escalar horizontalmente.

### Microservicios

| Servicio                      | Propósito                                                        | Puertos de ejemplo | BD por servicio | Notas                                                                     |
| ----------------------------- | ---------------------------------------------------------------- | -----------------: | --------------- | ------------------------------------------------------------------------- |
| **user-microservice**         | Registro y consulta de usuarios (Docente/Estudiante/Coordinador) |               8081 | H2/        | Valida correo institucional y contraseña.                                 |
| **project-microservice**      | Gestión de Proyecto y **evaluación del Formato A**               |         8082, 8092 | H2/        | Publica evento `formatoA.subido` tras subida. Máquina de estados (State). |
| **document-microservice**     | **Subida/descarga** de PDFs                                      |         8083, 8093 | H2/ + FS   | Guarda archivos en disco y metadatos en BD.                               |
| **messaging-microservice**    | Mensajería interna / acuse de lectura                            |               8084 | H2/        | Endpoints simples de lectura/actualización.                               |
| **notification-microservice** | **Consumers** de eventos (RabbitMQ)                              |               8085 | H2/        | Persiste notificaciones y loguea envío simulado.                          |

> Los puertos pueden variar según `application.properties`. Para carga pico se levantan múltiples instancias por servicio (p. ej. `project`: 8082 y 8092; `document`: 8083 y 8093).

### Diagramas (C4 + UML)

* **C1 Contexto, C2 Contenedores, C3 Componentes, C4 Clases**.
* Archivos/Enlaces de modelos están en los anexos del documento: https://docs.google.com/document/d/1tFdHnG_7d2V8XjRK3JmmQHO0TMXhMGXdm87kTYsU8uc/edit?usp=sharing

---

## ✅ Funcionalidades

* **Registro de Docentes** con validación de correo `@unicauca.edu.co` y reglas de contraseña.
* **Subida del Formato A** como PDF con metadatos completos.
* **Evaluación** del Formato A por Coordinación con estados y observaciones.
* **Reintentos** controlados (máx. 3); rechazo definitivo en el tercero.
* **Notificaciones** por evento a Jefatura/actores vía consumers de RabbitMQ.
* **Consulta de estado** del proyecto por estudiante.

---

## 📡 Integración por eventos

* **Exchange/cola:** RabbitMQ local.
* **Evento** `formatoA.subido` → payload típico `{ idProyecto, titulo, coordinadorEmail }`.
* **Consumidores:** `notification-microservice` (persiste y registra envío).
* Evento adicional sugerido: `anteproyecto.subido` para el siguiente hito.

---

## 🧪 Pruebas

* **Unitarias por servicio**: dominio (estados/entidades) y servicios.

### Nota JDK 25 (Mockito agente)

Para eliminar warnings y asegurar compatibilidad futura:

* **IntelliJ (JUnit Template → VM options):**

  ```
  -javaagent:"$USER_HOME$/.m2/repository/org/mockito/mockito-core/5.17.0/mockito-core-5.17.0.jar"
  ```
* **Maven (pom padre, Surefire):** precargar `-javaagent` de Mockito. Si usas JaCoCo, fusiona en `argLine`.

---

## 🧰 Tecnologías

* **Java 25**, **Spring Boot 3.5.x**, **Spring Cloud Stream**, **RabbitMQ**, **Spring Data JPA**, **H2/MySQL**.
* **OpenAPI/Swagger** expuesto por servicio.

---

## ▶️ Puesta en marcha (local)

### Prerrequisitos

* JDK 25
* Maven 3.9+
* RabbitMQ local (`localhost:5672`) y consola opcional (`http://localhost:15672`)
* (Opcional) MySQL si migras de H2 a producción


### 2) Levantar servicios

Preferiblemente en este orden:

```bash
user-microservice
project-microservice
document-microservice
messaging-microservice
notification-microservice
```

### 3) Documentación API (Swagger)

* `http://localhost:<puerto>/swagger-ui/index.html`

### 4) Colecciones Postman

* Colecciones por servicio en sus carpetas (ej.: `user-microservice/user_service_postman.json`).

---

## 🔗 Endpoints clave (resumen)

**Usuarios**

* `POST /api/v1/usuarios/docentes` — registro docente

**Proyectos**

* `POST /api/v1/proyectos/formato-a` — crea + publica evento
* `PATCH /api/v1/proyectos/{id}/evaluar` — aprobar/rechazar con observaciones

**Documentos**

* `POST /api/v1/documentos` — multipart: `idProyecto`, `tipoDocumento`, `archivo`
* `GET /api/v1/documentos/{id}/descargar`

**Mensajería**

* `PATCH /api/v1/mensajes/{id}/leido`

**Notificaciones**

* Consumers RabbitMQ (sin endpoints públicos obligatorios)

---

## 🎯 Escenario de calidad: escalabilidad (resumen)

Se simula hora pico con 100 subidas de Formato A casi simultáneas desde Postman Runner al project-microservice con notification inicialmente apagado; el servicio responde rápido confirmando la recepción y encola lo no crítico en RabbitMQ, la cola sube de forma controlada y, al encender notification-microservice, se drena hasta cero. Métricas: latencia promedio ≤ 1.5 s, ≥ 99% respuestas 200/201, profundidad de cola ~100 → 0 y drenaje ≤ 2 min. Resultado: el sistema absorbe el pico sin degradación visible y, si crece la demanda, se incorporan más instancias del servicio de proyectos sin cambios funcionales.

---

## 🧩 Patrones de diseño aplicados

* **State** para ciclo de vida del proyecto.
* **Strategy** para reglas de evaluación.
* **Observer / Event-Driven** con RabbitMQ.
* **Factory Method** en creación de entidades/usuarios.
* **Facade** en orquestaciones y gateway.
* **Repository, DTO, Adapter** donde corresponde.

---

## 🗂️ Estructura del repositorio

```
/notification-microservice
/project-microservice
/user-microservice
/document-microservice
/messaging-microservice
/docs (diagramas, anexos)
README.md (este archivo)
```

---

## 👥 Equipo

* Juan Fernando Portilla Collazos
* Edier Fabián Dorado Magón
* David Santiago Arias Narváez

Docentes: Wilson Pantoja Yépez, Brayan Daniel Perdomo Urbano

---

## 🧭 Trazabilidad y anexos

* Historias de usuario, épicas y backlog están documentadas en los anexos y tablero del curso.
* Modelos C4/UML y evidencias de ejecución se incluyen como recursos en `/docs` o referenciados en el documento del corte.
* Documento del corte: https://docs.google.com/document/d/1tFdHnG_7d2V8XjRK3JmmQHO0TMXhMGXdm87kTYsU8uc/edit?usp=sharing
* Jira: https://unicauca-team-dag79f44.atlassian.net/jira/software/projects/KAN/boards/1?atlOrigin=eyJpIjoiOTMwZTcyZTVjYzhlNGFmY2JjYTYyZTA1M2VmYmFjY2YiLCJwIjoiaiJ9
