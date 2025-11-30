# 👤 user-microservice

Microservicio encargado de la gestión de usuarios del sistema de Gestión de Trabajos de Grado.

## Funcionalidades
- Registro de docentes, estudiantes, coordinadores y jefes de departamento.
- Validación de email institucional (`@unicauca.edu.co`).
- Validación de contraseñas seguras (mínimo 6 caracteres, dígito, especial, mayúscula).
- Endpoint para validación cruzada (`/api/usuarios/validar`).
- Persistencia de datos entre reinicios (gracias a H2 en modo archivo).

## Tecnologías
- Spring Boot 3
- Spring Data JPA + H2 (modo archivo, persistente)
- Swagger UI para documentación

## Endpoints
- `POST /api/usuarios/docentes`
- `POST /api/usuarios/estudiantes`
- `POST /api/usuarios/coordinadores`
- `POST /api/usuarios/jefes-departamento`
- `GET /api/usuarios/validar?email=...`
- `GET /api/usuarios/{email}`

## Puerto
- `8081`
