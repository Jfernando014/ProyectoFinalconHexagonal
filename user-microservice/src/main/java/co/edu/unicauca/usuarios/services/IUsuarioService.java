package co.edu.unicauca.usuarios.services;

import co.edu.unicauca.usuarios.dto.UsuarioDetalleDTO;
import co.edu.unicauca.usuarios.models.Usuario;
import co.edu.unicauca.usuarios.models.enums.Rol;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import co.edu.unicauca.usuarios.util.UserAlreadyExistsException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IUsuarioService {
    // Nuevo método para registro único con roles
    Usuario registrarUsuario(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;

    // Métodos específicos (para compatibilidad)
    Usuario registrarDocente(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario registrarEstudiante(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario registrarCoordinador(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario registrarJefeDepartamento(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;

    // Métodos de consulta
    Usuario obtenerPorEmail(String email) throws InvalidUserDataException;
    boolean existeUsuario(String email);
    String obtenerRol(String email);
    Set<Rol> obtenerTodosRoles(String email);

    List<Usuario> buscar(String texto);

    Optional<UsuarioDetalleDTO> obtenerDetallePorEmail(String email);

}