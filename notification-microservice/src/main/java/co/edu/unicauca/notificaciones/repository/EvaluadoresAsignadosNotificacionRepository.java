package co.edu.unicauca.notificaciones.repository;

import co.edu.unicauca.notificaciones.models.EvaluadoresAsignadosNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluadoresAsignadosNotificacionRepository
        extends JpaRepository<EvaluadoresAsignadosNotificacion, Long> {
}
