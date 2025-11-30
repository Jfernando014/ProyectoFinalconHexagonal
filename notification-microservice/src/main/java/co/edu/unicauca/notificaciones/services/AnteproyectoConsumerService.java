package co.edu.unicauca.notificaciones.services;

import co.edu.unicauca.notificaciones.models.AnteproyectoNotificacion;
import co.edu.unicauca.notificaciones.repository.AnteproyectoNotificacionRepository;
import co.edu.unicauca.notificaciones.dto.AnteproyectoSubidoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnteproyectoConsumerService {

    @Autowired
    private AnteproyectoNotificacionRepository repo;

    @RabbitListener(queues = "${app.rabbitmq.queue.anteproyecto.subido}")
    public void onMessage(AnteproyectoSubidoEvent e) {
        // Mapear DTO -> Entidad
        AnteproyectoNotificacion notif = new AnteproyectoNotificacion();
        notif.setIdProyecto(e.getIdProyecto());
        notif.setTitulo(e.getTitulo());
        notif.setEstudianteEmail(e.getEstudianteEmail());
        notif.setTutor1Email(e.getTutor1Email());
        notif.setTutor2Email(e.getTutor2Email());
        notif.setJefeDepartamentoEmail(e.getJefeDepartamentoEmail());

        repo.save(notif);

        StringBuilder msg = new StringBuilder("\n=== NOTIFICACIONES ANTEPROYECTO ===\n");
        if (notif.getJefeDepartamentoEmail() != null) {
            msg.append(String.format(
                    "Para: %s | Asunto: Nuevo anteproyecto | Cuerpo: Revisar '%s' (ID: %d)\n",
                    notif.getJefeDepartamentoEmail(), notif.getTitulo(), notif.getIdProyecto()
            ));
        }
        if (notif.getEstudianteEmail() != null) {
            msg.append(String.format(
                    "Para: %s | Asunto: Confirmación anteproyecto | Cuerpo: Registrado '%s'\n",
                    notif.getEstudianteEmail(), notif.getTitulo()
            ));
        }
        if (notif.getTutor1Email() != null) {
            msg.append(String.format(
                    "Para: %s | Asunto: Asignación como tutor | Cuerpo: Tutor de '%s'\n",
                    notif.getTutor1Email(), notif.getTitulo()
            ));
        }
        if (notif.getTutor2Email() != null) {
            msg.append(String.format(
                    "Para: %s | Asunto: Asignación como tutor | Cuerpo: Tutor de '%s'\n",
                    notif.getTutor2Email(), notif.getTitulo()
            ));
        }

        log.info(msg.toString());
    }
}
