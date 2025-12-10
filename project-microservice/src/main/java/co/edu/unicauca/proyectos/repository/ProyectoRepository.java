package co.edu.unicauca.proyectos.repository;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<ProyectoGrado, Long> {
    List<ProyectoGrado> findByEstudiante1Email(String email);
    List<ProyectoGrado> findByDirectorEmail(String email);
    List<ProyectoGrado> findByAnteproyectoTokenIsNotNull();
    List<ProyectoGrado> findByEstadoActualIn(List<String> estados);
    List<ProyectoGrado> findByEstadoActual(String estado);

    List<ProyectoGrado> findByEvaluador1EmailOrEvaluador2Email(String evaluador1Email, String evaluador2Email);
}