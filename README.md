# Gestión del Proceso de Trabajo de Grado — Hexagonal (Tercer Corte)

Sistema para gestionar el flujo de trabajos de grado en la FIET (Universidad del Cauca). Este repositorio corresponde a la tercera entrega, donde la solución se consolida con:

- Arquitectura de microservicios + arquitectura hexagonal en el servicio de proyectos,
- Cliente de escritorio (DesktopClient JavaFX),
- Cutenticación/autorización con JWT,
- Soporte completo para Formato A, Anteproyecto y asignación de evaluadores con notificaciones asíncronas.
> Objetivo: registrar usuarios, gestionar envío y evaluación del Formato A, subida del Anteproyecto, delegación de evaluadores por Jefe de Departamento, manejo de documentos PDF, notificaciones y trazabilidad del estado del proyecto, todo integrado con un cliente de escritorio.

---

## 🔎 Vista rápida

* **Arquitectura:** Microservicios + mensajería (RabbitMQ) + arquitectura hexagonal en project-microservice)
* **Servicios:** users, projects, documents, messaging, notifications + DesktopClient (JavaFX)
* **Seguridad:** Autenticación y autorización con JWT (roles: ESTUDIANTE, DOCENTE, COORDINADOR, JEFE_DEPARTAMENTO)
* **Patrones:** State, Strategy, Factory Method, Observer (EDA), Facade, DTO, Repository, Adapter, Hexagonal
* **Pruebas:** unitarias por servicio (servicios y dominio) + pruebas manuales end-to-end con DesktopClient
* **Docs:** Swagger por servicio, modelos C4/UML, diagrama de bounded contexts y burndown chart

---

## 🧱 Arquitectura

**Desacoplamiento por bounded context** y **Database per Service**. Comunicación síncrona por **REST** y asíncrona por **eventos** con RabbitMQ. Los servicios son **stateless** (uso de JWT en lugar de sesiones) para escalar horizontalmente.

En el **project-microservice** se aplica arquitectura hexagonal:
El núcleo de dominio (ProyectoGrado, estados del proyecto, reglas de negocio) se separa de los adaptadores de entrada (REST controllers) y de los adaptadores de salida (repositorios JPA, clientes a otros microservicios, publicador de eventos a RabbitMQ).

### Microservicios

| Servicio                      | Propósito                                                                                     | Puertos de ejemplo | BD por servicio | Notas                                                                                                                                                        |
| ----------------------------- | --------------------------------------------------------------------------------------------- | -----------------: | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **user-microservice**         | Registro, autenticación y consulta de usuarios (Estudiante/Docente/Coordinador/Jefe)          |               8081 | H2/…            | Valida correo institucional y contraseña con reglas; genera **JWT** con email y rol.                                                                         |
| **project-microservice**      | Gestión de Proyecto de Grado, **Formato A**, **Anteproyecto** y **asignación de evaluadores** |         8082, 8092 | H2/…            | Núcleo hexagonal; máquina de estados (State); publica eventos `formatoA.subido`, `anteproyecto.subido`, `evaluadores.asignados`.                             |
| **document-microservice**     | **Subida/descarga** de documentos PDF asociados al proyecto                                   |         8083, 8093 | H2/ + FS        | Guarda archivos en disco y metadatos en BD. Consumido tanto por DesktopClient como por project-microservice.                                                 |
| **messaging-microservice**    | Mensajería interna / acuse de lectura                                                         |               8084 | H2/…            | Endpoints simples de lectura/actualización de mensajes internos entre actores.                                                                               |
| **notification-microservice** | **Consumers** de eventos RabbitMQ                                                             |               8085 | H2/…            | Persiste notificaciones y loguea envío simulado (correos a coordinador, jefe, evaluadores, estudiante).                                                      |
| **DesktopClient**             | Cliente de escritorio en **JavaFX**                                                           |        (local JVM) | —               | Consume los microservicios vía REST usando WebClient: dashboards por rol, subida de Formato A, subida de Anteproyecto, consulta de proyectos y evaluaciones. |


> Los puertos pueden variar según `application.properties`. Para carga pico se levantan múltiples instancias por servicio (p. ej. `project`: 8082 y 8092; `document`: 8083 y 8093).

### Diagramas (C4 + UML, Bounded Context, Burndown Chart)

* **C1 Contexto, C2 Contenedores, C3 Componentes, C4 Clases**.
* Archivos/Enlaces de modelos están en los anexos del documento: https://docs.google.com/document/d/1tFdHnG_7d2V8XjRK3JmmQHO0TMXhMGXdm87kTYsU8uc/edit?usp=sharing

---

## ✅ Funcionalidades
### Funcionalidades heredadas (segundo corte)
* **Registro de Docentes** con validación de correo `@unicauca.edu.co` y reglas de contraseña.
* **Subida del Formato A** como PDF con metadatos completos.
* **Evaluación** del Formato A por Coordinación con estados y observaciones.
* **Reintentos** controlados (máx. 3); rechazo definitivo en el tercero.
* **Notificaciones** por evento a Jefatura/actores vía consumers de RabbitMQ.
* **Consulta de estado** del proyecto por estudiante.

### Funcionalidades nuevas/consolidadas (tercer corte)
* **Cliente de escritorio (DesktopClient JavaFX)** con vistas diferenciadas para:
    - Estudiante: subir Formato A y Anteproyecto, ver estado.
    - Docente: registrar Formato A como director, ver proyectos y evaluaciones.
    - Coordinador: evaluar Formato A.
    -  Jefe de Departamento: ver anteproyectos y asignar dos evaluadores por anteproyecto.
* **Subida de Anteproyecto:**
    - Desde DesktopClient, selección de proyecto con Formato A aprobado, carga de PDF y metadatos.
    - El project-microservice actualiza estado del proyecto y notifica por evento.
* **Asignación de evaluadores:**
    - El Jefe de Departamento asigna dos docentes evaluadores a un anteproyecto.
    - Se emite evento de evaluadores.asignados y el microservicio de notificaciones genera avisos simulados para los docentes involucrados.
* **Autenticación y autorización con JWT:**
    - Login contra user-microservice → se genera token JWT firmado con rol.
    - DesktopClient almacena el token y lo envía en todas las peticiones.
    - Control de acceso por rol en cada microservicio (@PreAuthorize("hasRole('DOCENTE')"), etc.).
* **Servicio de proyectos con arquitectura hexagonal:**
    - Puerto de entrada: fachada de casos de uso IProyectoServiceFacade.
    - Puertos de salida: ProyectoRepository, clientes a Usuarios/Documentos, publicador de eventos a RabbitMQ.
    - Dominio independiente de infraestructura (REST, JPA, RabbitMQ).
---

## 📡 Integración por eventos

* **Exchange/cola:** RabbitMQ local (Event Bus).
* **Eventos principales publicados desde `project-microservice`:**
  * `formatoA.subido` → `{ idProyecto, titulo, coordinadorEmail }`
  * `formatoA.evaluado` → `{ idProyecto, aprobado, observaciones, estudianteEmail }`
  * `anteproyecto.subido` → `{ idProyecto, titulo, jefeDepartamentoEmail }`
  * `evaluadores.asignados` → `{ idProyecto, evaluador1Email, evaluador2Email, estudianteEmail }`
* **Consumidores:**
  * `notification-microservice` (persiste notificaciones y registra envío).
  * `messaging-microservice` (puede reaccionar a ciertos eventos para mensajes internos / bandejas). 
* El diseño mantiene el flujo de notificaciones desacoplado del tiempo de respuesta de las operaciones críticas (subida de Formato A / Anteproyecto, asignación de evaluadores).

---

## 🧪 Pruebas

* **Unitarias por servicio**: dominio (estados/entidades) y servicios.
* **Pruebas manuales end-to-end con DesktopClient**: Escenario completo por rol:
    * Estudiante/Docente sube Formato A.
    * Coordinador evalúa (aprobado/rechazado).
    * Estudiante sube Anteproyecto.
    * Jefe de Departamento asigna evaluadores.
    * Notificaciones se registran vía eventos.
* **Validación de seguridad**: Acceso a endpoints protegidos solo con JWT válido y verificación de reglas de rol (ej. un estudiante no puede asignar evaluadores).

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
* **Frontend de escritorio:** JavaFX + WebClient (reactor-netty)
* **Documentación:** OpenAPI/Swagger por servicio.
* **Mensajería:** RabbitMQ local (spring-cloud-stream / spring-amqp).

---

## ▶️ Puesta en marcha (local)

### Prerrequisitos

* JDK 25
* Maven 3.9+
* RabbitMQ local (`localhost:5672`) y consola opcional (`http://localhost:15672`)
* (Opcional) MySQL si migras de H2 a producción
* JavaFX configurado en el IDE para ejecutar DesktopClient


### 1) Levantar servicios

Preferiblemente en este orden:

```bash
user-microservice
project-microservice
document-microservice
messaging-microservice
notification-microservice
```

### 2) Ejecutar DesktopClient

Desde el módulo DesktopClient:

```bash
mvn clean javafx:run
```
(o ejecutando la clase Main desde el IDE.)

### 3) Documentación API (Swagger)

* `http://localhost:<puerto>/swagger-ui/index.html`

### 4) Colecciones Postman

* Colecciones por servicio en sus carpetas (ej.: `user-microservice/user_service_postman.json`).

---

## 🔗 Endpoints clave (resumen)

En la respectiva documentación Swagger.

---

## 🎯 Escenario de calidad: escalabilidad (resumen)

Para este corte se refuerza la seguridad y se mantiene la escalabilidad:

**Seguridad:**
- Todos los microservicios críticos exigen JWT en el encabezado Authorization: Bearer <token>.
- El token se genera en user-microservice tras validar credenciales con BCrypt.
- Los controladores aplican restricciones por rol (hasRole('DOCENTE'), hasRole('JEFE_DEPARTAMENTO'), etc.), impidiendo que un usuario ejecute operaciones no autorizadas (por ejemplo, que un estudiante asigne evaluadores).

**Escalabilidad:**
- Se mantiene el escenario de hora pico de subidas de Formato A/Anteproyecto con múltiples instancias del project-microservice.
- Las operaciones críticas responden rápido mientras las notificaciones se procesan de forma asíncrona vía RabbitMQ.
- Los servicios siguen siendo stateless, por lo que se pueden escalar horizontalmente sin dependencia de sesión de servidor.

---

## 🧩 Patrones de diseño aplicados

* **State** para el ciclo de vida del proyecto (estados de Formato A, Anteproyecto, etc.).
* **Strategy** para reglas de evaluación y políticas de negocio.
* **Observer / Event-Driven** con RabbitMQ y microservicio de notificaciones.
* **Factory Method** en la creación de entidades/usuarios.
* **Facade** para orquestación de casos de uso (IProyectoServiceFacade).
* **Repository, DTO, Adapter** donde corresponde (repositorios JPA, mapeo a DTOs, adaptadores REST).
* **Arquitectura Hexagonal** en `project-microservice`:
  * Puertos de entrada (fachada de aplicación)
  * Puertos de salida (repositorio, clientes externos, publicador de eventos),
  * Adaptadores de infraestructura (REST controllers, JPA, RabbitMQ).

---

## 🗂️ Estructura del repositorio

```
/notification-microservice
/project-microservice
/user-microservice
/document-microservice
/messaging-microservice
/DesktopClient
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
* Modelos C4/UML, diagrama de bounded contexts, burndown chart y evidencias de ejecución se incluyen como recursos en `/docs` o referenciados en el documento del tercer corte.
* Documento del corte: https://docs.google.com/document/d/1uLtD23AlhSjXwcr_A6huYe2L6MK_ds1rBo8jWqsEGh0/edit?usp=sharing
* Jira: https://unicauca-team-dag79f44.atlassian.net/jira/software/projects/KAN/boards/1?atlOrigin=eyJpIjoiOTMwZTcyZTVjYzhlNGFmY2JjYTYyZTA1M2VmYmFjY2YiLCJwIjoiaiJ9
