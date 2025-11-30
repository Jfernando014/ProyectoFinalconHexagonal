package co.edu.unicauca.proyectos.services.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "user-service", url = "${user.service.url:http://localhost:8081}")
public interface UsuariosClient {

    @GetMapping("/api/usuarios/validar")
    Map<String, Object> validarUsuario(@RequestParam("email") String email);
}
