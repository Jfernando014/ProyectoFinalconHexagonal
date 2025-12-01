package co.edu.unicauca.usuarios.vista;

import co.edu.unicauca.usuarios.dto.LoginRequest;
import co.edu.unicauca.usuarios.models.Usuario;
import co.edu.unicauca.usuarios.security.JwtTokenProvider;
import co.edu.unicauca.usuarios.services.IUsuarioService;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IUsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(IUsuarioService usuarioService, JwtTokenProvider jwtTokenProvider) {
        this.usuarioService = usuarioService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Usuario u = usuarioService.obtenerPorEmail(request.getEmail());

            if (u == null || !u.getPassword().equals(request.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
            }

            String rol = usuarioService.obtenerRol(u.getEmail());
            String token = jwtTokenProvider.generateToken(u.getEmail(), rol);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", u.getEmail(),
                    "rol", rol
            ));
        } catch (InvalidUserDataException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        String rol = usuarioService.obtenerRol(email);

        return ResponseEntity.ok(Map.of(
                "email", email,
                "rol", rol
        ));
    }
}
