package co.edu.unicauca.usuarios.vista;

import co.edu.unicauca.usuarios.dto.*;
import co.edu.unicauca.usuarios.models.Usuario;
import co.edu.unicauca.usuarios.models.enums.Rol;
import co.edu.unicauca.usuarios.services.IUsuarioService;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import co.edu.unicauca.usuarios.util.UserAlreadyExistsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para gestión de usuarios - CRUD de usuarios con diferentes roles")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/search")
    @Operation(
            summary = "Buscar usuarios",
            description = "Busca usuarios por término de búsqueda (nombre, apellido, email)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = java.util.List.class),
                            examples = @ExampleObject(value = """
                    [
                        {
                            "email": "juan.perez@unicauca.edu.co",
                            "nombreCompleto": "Juan Pérez"
                        }
                    ]
                    """)
                    )
            )
    })
    public List<Map<String, Object>> search(
            @Parameter(description = "Término de búsqueda", required = true, example = "Juan")
            @RequestParam("q") String q) {

        return usuarioService.buscar(q)
                .stream()
                .map(u -> Map.<String, Object>of(
                        "email", u.getEmail(),
                        "nombreCompleto", u.getNombres() + " " + u.getApellidos()
                ))
                .toList();
    }

    @PostMapping("/registro")
    @Operation(
            summary = "Registrar usuario con múltiples roles",
            description = "Registra un nuevo usuario con uno o varios roles. Roles disponibles: ESTUDIANTE, DOCENTE, COORDINADOR, JEFE_DEPARTAMENTO"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = """
                    {
                        "email": "juan.perez@unicauca.edu.co",
                        "nombres": "Juan",
                        "apellidos": "Pérez",
                        "roles": ["ESTUDIANTE"]
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error en la solicitud",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Usuario ya existe", value = """
                        {"error": "El usuario ya existe: email@unicauca.edu.co"}
                        """),
                                    @ExampleObject(name = "Datos inválidos", value = """
                        {"error": "Datos inválidos: Campo requerido"}
                        """),
                                    @ExampleObject(name = "Sin roles", value = """
                        {"error": "Debe especificar al menos un rol"}
                        """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del usuario a registrar",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RegistroUsuarioDTO.class),
                    examples = @ExampleObject(value = """
                {
                    "email": "nuevo.usuario@unicauca.edu.co",
                    "password": "password123",
                    "nombres": "Ana",
                    "apellidos": "García",
                    "celular": "3123456789",
                    "programa": "Ingeniería de Sistemas",
                    "roles": ["ESTUDIANTE", "DOCENTE"]
                }
                """)
            )
    )
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroUsuarioDTO req) {
        try {
            if (req.getRoles() == null || req.getRoles().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe especificar al menos un rol"));
            }

            Usuario usuario = new Usuario();
            usuario.setEmail(req.getEmail());
            usuario.setPassword(req.getPassword());
            usuario.setNombres(req.getNombres());
            usuario.setApellidos(req.getApellidos());
            usuario.setCelular(req.getCelular());
            usuario.setPrograma(req.getPrograma());
            usuario.setRoles(req.getRoles());

            Usuario saved = usuarioService.registrarUsuario(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("email", saved.getEmail());
            response.put("nombres", saved.getNombres());
            response.put("apellidos", saved.getApellidos());
            response.put("roles", saved.getRoles());

            return ResponseEntity.ok(response);

        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya existe: " + e.getMessage()));
        } catch (InvalidUserDataException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/docentes")
    @Operation(
            summary = "Registrar docente",
            description = "Registra un nuevo usuario con rol DOCENTE"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Docente registrado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error en la solicitud"
            )
    })
    public ResponseEntity<?> registrarDocente(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del docente",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DocenteRequest.class)
                    )
            )
            @RequestBody DocenteRequest req) {

        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail(req.getEmail());
        dto.setPassword(req.getPassword());
        dto.setNombres(req.getNombres());
        dto.setApellidos(req.getApellidos());
        dto.setCelular(req.getCelular());
        dto.setPrograma(req.getPrograma());
        dto.setRoles(Set.of(Rol.DOCENTE));

        return registrarUsuario(dto);
    }

    @PostMapping("/estudiantes")
    @Operation(summary = "Registrar estudiante")
    public ResponseEntity<?> registrarEstudiante(@RequestBody EstudianteRequest req) {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail(req.getEmail());
        dto.setPassword(req.getPassword());
        dto.setNombres(req.getNombres());
        dto.setApellidos(req.getApellidos());
        dto.setCelular(req.getCelular());
        dto.setPrograma(req.getPrograma());
        dto.setRoles(Set.of(Rol.ESTUDIANTE));

        return registrarUsuario(dto);
    }

    @PostMapping("/coordinadores")
    @Operation(summary = "Registrar coordinador")
    public ResponseEntity<?> registrarCoordinador(@RequestBody CoordinadorRequest req) {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail(req.getEmail());
        dto.setPassword(req.getPassword());
        dto.setNombres(req.getNombres());
        dto.setApellidos(req.getApellidos());
        dto.setCelular(req.getCelular());
        dto.setPrograma(req.getPrograma());
        dto.setRoles(Set.of(Rol.COORDINADOR));

        return registrarUsuario(dto);
    }

    @PostMapping("/jefes-departamento")
    @Operation(summary = "Registrar jefe de departamento")
    public ResponseEntity<?> registrarJefe(@RequestBody JefeDepartamentoRequest req) {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setEmail(req.getEmail());
        dto.setPassword(req.getPassword());
        dto.setNombres(req.getNombres());
        dto.setApellidos(req.getApellidos());
        dto.setCelular(req.getCelular());
        dto.setPrograma(req.getPrograma());
        dto.setRoles(Set.of(Rol.JEFE_DEPARTAMENTO));

        return registrarUsuario(dto);
    }

    @GetMapping("/validar")
    @Operation(
            summary = "Validar existencia de usuario",
            description = "Verifica si un usuario existe y retorna sus roles"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultado de validación",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Usuario existe", value = """
                        {
                            "existe": true,
                            "roles": ["ESTUDIANTE", "DOCENTE"]
                        }
                        """),
                                    @ExampleObject(name = "Usuario no existe", value = """
                        {
                            "existe": false
                        }
                        """)
                            }
                    )
            )
    })
    public ResponseEntity<?> validar(
            @Parameter(description = "Email del usuario a validar", required = true, example = "usuario@unicauca.edu.co")
            @RequestParam("email") String email) {

        boolean existe = usuarioService.existeUsuario(email);
        Set<Rol> roles = usuarioService.obtenerTodosRoles(email);

        Map<String, Object> response = new HashMap<>();
        response.put("existe", existe);
        if (existe) {
            response.put("roles", roles);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{email}")
    @Operation(
            summary = "Obtener usuario por email",
            description = "Retorna la información completa de un usuario"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                        "email": "juan.perez@unicauca.edu.co",
                        "nombres": "Juan",
                        "apellidos": "Pérez",
                        "celular": "3123456789",
                        "programa": "Ingeniería de Sistemas",
                        "roles": ["ESTUDIANTE"]
                    }
                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {"error": "Usuario no encontrado"}
                    """)
                    )
            )
    })
    public ResponseEntity<?> obtenerUsuario(
            @Parameter(description = "Email del usuario", required = true, example = "usuario@unicauca.edu.co")
            @PathVariable("email") String email) {

        try {
            var usuario = usuarioService.obtenerPorEmail(email);
            Map<String, Object> response = new HashMap<>();
            response.put("email", usuario.getEmail());
            response.put("nombres", usuario.getNombres());
            response.put("apellidos", usuario.getApellidos());
            response.put("celular", usuario.getCelular());
            response.put("programa", usuario.getPrograma());
            response.put("roles", usuario.getRoles());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/detalle")
    @Operation(
            summary = "Obtener detalle de usuario",
            description = "Retorna información detallada del usuario usando DTO específico"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalle del usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioDetalleDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    public ResponseEntity<UsuarioDetalleDTO> obtenerDetallePorEmail(
            @Parameter(description = "Email del usuario", required = true, example = "usuario@unicauca.edu.co")
            @RequestParam String email) {

        return usuarioService.obtenerDetallePorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}