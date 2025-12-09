package co.edu.unicauca.usuarios.services;

import co.edu.unicauca.usuarios.dto.UsuarioDetalleDTO;
import co.edu.unicauca.usuarios.models.Usuario;
import co.edu.unicauca.usuarios.models.enums.Rol;
import co.edu.unicauca.usuarios.repository.UsuarioRepository;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import co.edu.unicauca.usuarios.util.UserAlreadyExistsException;
import co.edu.unicauca.usuarios.util.security.PasswordValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // ---------- Registro único ----------
    @Override
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        validarUsuario(usuario);
        normalizar(usuario);

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    // ---------- Métodos específicos (para compatibilidad) ----------
    @Override
    public Usuario registrarDocente(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        // Este método ya no se usará, pero lo mantenemos por compatibilidad
        // Ahora se usa registrarUsuario con los roles en el conjunto
        return registrarUsuario(usuario);
    }

    @Override
    public Usuario registrarEstudiante(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        return registrarUsuario(usuario);
    }

    @Override
    public Usuario registrarCoordinador(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        return registrarUsuario(usuario);
    }

    @Override
    public Usuario registrarJefeDepartamento(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        return registrarUsuario(usuario);
    }

    // ---------- Consulta ----------
    @Override
    public Usuario obtenerPorEmail(String email) throws InvalidUserDataException {
        if (email == null || email.isBlank()) {
            throw new InvalidUserDataException("Email requerido");
        }
        return usuarioRepository.findById(email)
                .orElseThrow(() -> new InvalidUserDataException("Usuario no encontrado"));
    }

    @Override
    public boolean existeUsuario(String email) {
        if (email == null) return false;
        return usuarioRepository.existsById(email);
    }

    @Override
    public String obtenerRol(String email) {
        // Para compatibilidad, devuelve el primer rol si existe
        Usuario usuario = usuarioRepository.findById(email).orElse(null);
        if (usuario == null || usuario.getRoles().isEmpty()) {
            return "DESCONOCIDO";
        }
        return usuario.getRoles().iterator().next().name();
    }

    @Override
    public Set<Rol> obtenerTodosRoles(String email) {
        Usuario usuario = usuarioRepository.findById(email).orElse(null);
        if (usuario == null) {
            return Set.of();
        }
        return usuario.getRoles();
    }

    @Override
    public List<Usuario> buscar(String texto) {
        return usuarioRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrEmailContainsIgnoreCase(texto,texto,texto);
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
        // Validar que tenga al menos un rol
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            throw new InvalidUserDataException("El usuario debe tener al menos un rol");
        }
    }

    private void normalizar(Usuario u) {
        if (u.getNombres() != null) u.setNombres(u.getNombres().trim());
        if (u.getApellidos() != null) u.setApellidos(u.getApellidos().trim());
        if (u.getPrograma() != null) u.setPrograma(u.getPrograma().trim());
        if (u.getCelular() != null) u.setCelular(u.getCelular().trim());
    }

    public boolean verificarPassword(String email, String password) {
        Usuario usuario = usuarioRepository.findById(email).orElse(null);
        if (usuario == null) return false;
        return passwordEncoder.matches(password, usuario.getPassword());
    }

    // ---------- NUEVO: detalle por email ----------
    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioDetalleDTO> obtenerDetallePorEmail(String email) {
        return usuarioRepository
                .findByEmail(email)
                .map(this::mapToDetalleDTO);
    }

    /**
     * Mapea la entidad Usuario al DTO UsuarioDetalleDTO.
     **/
    private UsuarioDetalleDTO mapToDetalleDTO(Usuario u) {
        String rolPrincipal = null;
        if (u.getRoles() != null && !u.getRoles().isEmpty()) {
            rolPrincipal = u.getRoles().iterator().next().name();
        }

        return new UsuarioDetalleDTO(
                u.getNombres(),
                u.getApellidos(),
                u.getEmail(),
                u.getCelular(),
                rolPrincipal,
                u.getPrograma()
        );
    }


}