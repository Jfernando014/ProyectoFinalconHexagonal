package co.edu.unicauca.usuarios.vista;

import co.edu.unicauca.usuarios.dto.LoginRequest;
import co.edu.unicauca.usuarios.models.Usuario;
import co.edu.unicauca.usuarios.models.enums.Rol;
import co.edu.unicauca.usuarios.security.JwtTokenProvider;
import co.edu.unicauca.usuarios.services.IUsuarioService;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(IUsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Usuario usuario = usuarioService.obtenerPorEmail(loginRequest.getEmail());

            // Verificar contraseña
            if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
            }

            // Obtener todos los roles del usuario
            Set<Rol> roles = usuario.getRoles();

            // Convertir roles a lista de strings para el token (separados por coma)
            String rolesString = roles.stream()
                    .map(Rol::name)
                    .collect(Collectors.joining(","));

            // Generar token con todos los roles
            String token = tokenProvider.generateToken(usuario.getEmail(), rolesString);

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", usuario.getEmail());
            response.put("nombres", usuario.getNombres());
            response.put("apellidos", usuario.getApellidos());
            response.put("roles", roles);

            return ResponseEntity.ok(response);

        } catch (InvalidUserDataException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }
}