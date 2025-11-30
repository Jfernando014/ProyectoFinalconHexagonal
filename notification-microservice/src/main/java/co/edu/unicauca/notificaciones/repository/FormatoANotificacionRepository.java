package co.edu.unicauca.notificaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unicauca.notificaciones.models.FormatoANotificacion;

@Repository
public interface FormatoANotificacionRepository extends JpaRepository<FormatoANotificacion, Long> {
}