package co.edu.unicauca.usuarios.vista;

import co.edu.unicauca.usuarios.dto.*;
import co.edu.unicauca.usuarios.models.Usuario;
import co.edu.unicauca.usuarios.models.enums.Rol;
import co.edu.unicauca.usuarios.services.IUsuarioService;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import co.edu.unicauca.usuarios.util.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(@RequestParam("q") String q) {
        return usuarioService.buscar(q)
                .stream()
                .map(u -> Map.<String, Object>of(
                        "email", u.getEmail(),
                        "nombreCompleto", u.getNombres() + " " + u.getApellidos()
                ))
                .toList();
    }

    // ------- Registro Único para múltiples roles -------
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroUsuarioDTO req) {
        try {
            // Validar que al menos tenga un rol
            if (req.getRoles() == null || req.getRoles().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe especificar al menos un rol"));
            }

            // Crear el usuario
            Usuario usuario = new Usuario();
            usuario.setEmail(req.getEmail());
            usuario.setPassword(req.getPassword());
            usuario.setNombres(req.getNombres());
            usuario.setApellidos(req.getApellidos());
            usuario.setCelular(req.getCelular());
            usuario.setPrograma(req.getPrograma());
            usuario.setRoles(req.getRoles());

            Usuario saved = usuarioService.registrarUsuario(usuario);

            // Preparar respuesta
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
    public ResponseEntity<?> registrarDocente(@RequestBody DocenteRequest req) {
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

    // ------- Validación cruzada para otros MS -------
    @GetMapping("/validar")
    public ResponseEntity<?> validar(@RequestParam("email") String email) {
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
    public ResponseEntity<?> obtenerUsuario(@PathVariable("email") String email) {
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
    public ResponseEntity<UsuarioDetalleDTO> obtenerDetallePorEmail(
            @RequestParam String email) {

        return usuarioService.obtenerDetallePorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}