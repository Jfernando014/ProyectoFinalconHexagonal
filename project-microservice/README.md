# 📁 project-microservice

Microservicio que gestiona el ciclo de vida de los proyectos de grado, incluyendo estados, evaluaciones y anteproyectos.

## Funcionalidades
- Creación de proyectos de grado.
- Evaluación de Formato A (aprobar/rechazar).
- Reintento de Formato A (hasta 3 intentos).
- Subida de anteproyecto.
- Consulta de proyectos por estudiante o jefe de departamento.
- Publicación de eventos asíncronos (RabbitMQ).

## Patrones de diseño
- State (estados del proyecto)
- Template Method (evaluación)
- Facade (orquestación)
- Adapter (comunicación con user-service vía Feign)

## Tecnologías
- Spring Boot 3
- Spring Cloud OpenFeign
- Spring AMQP + RabbitMQ
- H2 (base de datos en memoria)

## Endpoints
- `POST /api/proyectos`
- `POST /api/proyectos/{id}/evaluar`
- `POST /api/proyectos/{id}/reintentar`
- `POST /api/proyectos/{id}/anteproyecto`
- `GET /api/proyectos/estudiante/{email}`
- `GET /api/proyectos/anteproyectos/jefe/{email}`

## Puerto
- `8082`
