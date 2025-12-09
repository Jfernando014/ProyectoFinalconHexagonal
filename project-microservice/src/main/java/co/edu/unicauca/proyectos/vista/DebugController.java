package co.edu.unicauca.proyectos.vista;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/auth")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> debug(Authentication auth) {
        return Map.of(
                "principal", auth.getPrincipal(),
                "authorities", auth.getAuthorities()
        );
    }
}