package co.edu.unicauca.usuarios.services;

import co.edu.unicauca.usuarios.models.*;
import co.edu.unicauca.usuarios.models.enums.Rol;
import co.edu.unicauca.usuarios.repository.UsuarioRepository;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import co.edu.unicauca.usuarios.util.UserAlreadyExistsException;
import co.edu.unicauca.usuarios.util.security.PasswordValidator;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ---------- Registro ----------

    @Override
    public Usuario registrarDocente(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        if (!(usuario instanceof Docente)) throw new InvalidUserDataException("Tipo inválido: se esperaba DOCENTE");
        validarUsuario(usuario);
        normalizar(usuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrarEstudiante(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        if (!(usuario instanceof Estudiante)) throw new InvalidUserDataException("Tipo inválido: se esperaba ESTUDIANTE");
        validarUsuario(usuario);
        normalizar(usuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrarCoordinador(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        if (!(usuario instanceof Coordinador)) throw new InvalidUserDataException("Tipo inválido: se esperaba COORDINADOR");
        validarUsuario(usuario);
        normalizar(usuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrarJefeDepartamento(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        if (!(usuario instanceof JefeDepartamento)) throw new InvalidUserDataException("Tipo inválido: se esperaba JEFE_DEPARTAMENTO");
        validarUsuario(usuario);
        normalizar(usuario);
        return usuarioRepository.save(usuario);
    }

    // ---------- Consulta ----------

    @Override
    public Usuario obtenerPorEmail(String email) throws InvalidUserDataException {
        if (email == null || email.isBlank()) throw new InvalidUserDataException("Email requerido");
        return usuarioRepository.findById(email).orElseThrow(() -> new InvalidUserDataException("Usuario no encontrado"));
    }

    @Override
    public boolean existeUsuario(String email) {
        if (email == null) return false;
        return usuarioRepository.existsById(email);
    }

    @Override
    public String obtenerRol(String email) {
        return usuarioRepository.findById(email)
                .map(u -> {
                    if (u instanceof Docente) return Rol.DOCENTE.name();
                    if (u instanceof Estudiante) return Rol.ESTUDIANTE.name();
                    if (u instanceof Coordinador) return Rol.COORDINADOR.name();
                    if (u instanceof JefeDepartamento) return Rol.JEFE_DEPARTAMENTO.name();
                    return "DESCONOCIDO";
                })
                .orElse("DESCONOCIDO");
    }

    // ---------- Util ----------

    private void validarUsuario(Usuario usuario) throws InvalidUserDataException, UserAlreadyExistsException {
        if (usuario.getEmail() == null || !usuario.getEmail().endsWith("@unicauca.edu.co")) {
            throw new InvalidUserDataException("El email debe ser del dominio @unicauca.edu.co");
        }
        if (!PasswordValidator.isValid(usuario.getPassword())) {
            throw new InvalidUserDataException("La contraseña no cumple con los requisitos");
        }
        if (usuarioRepository.existsById(usuario.getEmail())) {
            throw new UserAlreadyExistsException("El usuario ya existe");
        }
    }

    private void normalizar(Usuario u){
        // Persistimos el email tal como llega para no romper clave primaria ya usada en otros MS.
        // Si quieres case-insensitive, cambia a: u.setEmail(u.getEmail().toLowerCase());
        if (u.getNombres() != null) u.setNombres(u.getNombres().trim());
        if (u.getApellidos() != null) u.setApellidos(u.getApellidos().trim());
        if (u.getPrograma() != null) u.setPrograma(u.getPrograma().trim());
        if (u.getCelular() != null) u.setCelular(u.getCelular().trim());
    }
}
