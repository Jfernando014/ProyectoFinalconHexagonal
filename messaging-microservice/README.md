# 💬 messaging-microservice

Microservicio encargado de la comunicación entre usuarios del sistema (mensajería interna y notificaciones asociadas a los proyectos).

## Funcionalidades

- Envío y recepción de mensajes entre usuarios.

- Almacenamiento de conversaciones.

- Notificación a los destinatarios mediante eventos o colas de mensajería.

- Búsqueda de mensajes por usuario o conversación.

## Tecnologías

- Spring Boot 3

- Spring Data JPA + H2

- RabbitMQ (para mensajería asíncrona)

- Swagger UI

## Endpoints

- POST /api/mensajes — Enviar mensaje.

- GET /api/mensajes/{id} — Obtener mensaje por ID.

- GET /api/mensajes/usuario/{email} — Mensajes de un usuario.

- DELETE /api/mensajes/{id} — Eliminar mensaje.
## Puerto
- `8085`
