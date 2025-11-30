package co.edu.unicauca.usuarios.services;

import co.edu.unicauca.usuarios.models.Usuario;
import co.edu.unicauca.usuarios.util.InvalidUserDataException;
import co.edu.unicauca.usuarios.util.UserAlreadyExistsException;

public interface IUsuarioService {
    Usuario registrarDocente(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario registrarEstudiante(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario registrarCoordinador(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario registrarJefeDepartamento(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario obtenerPorEmail(String email) throws InvalidUserDataException;
    boolean existeUsuario(String email);
    String obtenerRol(String email);
}