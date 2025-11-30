package co.edu.unicauca.usuarios.vista;

import co.edu.unicauca.usuarios.dto.CoordinadorRequest;
import co.edu.unicauca.usuarios.dto.DocenteRequest;
import co.edu.unicauca.usuarios.dto.EstudianteRequest;
import co.edu.unicauca.usuarios.dto.JefeDepartamentoRequest;
import co.edu.unicauca.usuarios.models.*;
import co.edu.unicauca.usuarios.services.IUsuarioService;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import co.edu.unicauca.usuarios.util.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;
    public UsuarioController(IUsuarioService usuarioService){ this.usuarioService = usuarioService; }

    // ------- Registro -------

    @PostMapping("/docentes")
    public ResponseEntity<?> registrarDocente(@RequestBody DocenteRequest req) {
        try {
            Docente d = new Docente();
            d.setEmail(req.getEmail());
            d.setPassword(req.getPassword());
            d.setNombres(req.getNombres());
            d.setApellidos(req.getApellidos());
            d.setCelular(req.getCelular());
            d.setPrograma(req.getPrograma());
            d.setTipoDocente(req.getTipoDocente());
            var saved = usuarioService.registrarDocente(d);
            return ResponseEntity.ok(Map.of("email", saved.getEmail(), "rol", "DOCENTE"));
        } catch (UserAlreadyExistsException | InvalidUserDataException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/estudiantes")
    public ResponseEntity<?> registrarEstudiante(@RequestBody EstudianteRequest req) {
        try {
            Estudiante e = new Estudiante();
            e.setEmail(req.getEmail());
            e.setPassword(req.getPassword());
            e.setNombres(req.getNombres());
            e.setApellidos(req.getApellidos());
            e.setCelular(req.getCelular());
            e.setPrograma(req.getPrograma());
            var saved = usuarioService.registrarEstudiante(e);
            return ResponseEntity.ok(Map.of("email", saved.getEmail(), "rol", "ESTUDIANTE"));
        } catch (UserAlreadyExistsException | InvalidUserDataException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/coordinadores")
    public ResponseEntity<?> registrarCoordinador(@RequestBody CoordinadorRequest req) {
        try {
            Coordinador c = new Coordinador();
            c.setEmail(req.getEmail());
            c.setPassword(req.getPassword());
            c.setNombres(req.getNombres());
            c.setApellidos(req.getApellidos());
            c.setCelular(req.getCelular());
            c.setPrograma(req.getPrograma());
            var saved = usuarioService.registrarCoordinador(c);
            return ResponseEntity.ok(Map.of("email", saved.getEmail(), "rol", "COORDINADOR"));
        } catch (UserAlreadyExistsException | InvalidUserDataException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/jefes-departamento")
    public ResponseEntity<?> registrarJefe(@RequestBody JefeDepartamentoRequest req) {
        try {
            JefeDepartamento j = new JefeDepartamento();
            j.setEmail(req.getEmail());
            j.setPassword(req.getPassword());
            j.setNombres(req.getNombres());
            j.setApellidos(req.getApellidos());
            j.setCelular(req.getCelular());
            j.setPrograma(req.getPrograma());
            var saved = usuarioService.registrarJefeDepartamento(j);
            return ResponseEntity.ok(Map.of("email", saved.getEmail(), "rol", "JEFE_DEPARTAMENTO"));
        } catch (UserAlreadyExistsException | InvalidUserDataException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ------- Validación cruzada para otros MS -------

    @GetMapping("/validar")
    public ResponseEntity<?> validar(@RequestParam("email") String email){
        boolean existe = usuarioService.existeUsuario(email);
        String rol = usuarioService.obtenerRol(email);
        return ResponseEntity.ok(Map.of("existe", existe, "rol", existe ? rol : null));
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable("email") String email) {
        try {
            var usuario = usuarioService.obtenerPorEmail(email);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
