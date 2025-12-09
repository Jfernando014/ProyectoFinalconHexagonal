package co.edu.unicauca.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.unicauca.usuarios.models.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    List<Usuario> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrEmailContainsIgnoreCase(
            String nombres,
            String apellidos,
            String email
    );

    Optional<Usuario> findByEmail(String email);
}
