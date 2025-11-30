package co.edu.unicauca.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.unicauca.usuarios.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}
