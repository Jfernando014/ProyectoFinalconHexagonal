package co.edu.unicauca.mensajeria.repository;

import co.edu.unicauca.mensajeria.models.MensajeInterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeInternoRepository extends JpaRepository<MensajeInterno, Long> {
    List<MensajeInterno> findByRemitenteEmail(String remitenteEmail);
    List<MensajeInterno> findByDestinatariosEmailContaining(String destinatarioEmail);
}