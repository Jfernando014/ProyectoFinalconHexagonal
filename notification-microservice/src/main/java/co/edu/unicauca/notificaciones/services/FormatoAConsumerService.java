package co.edu.unicauca.notificaciones.services;

import co.edu.unicauca.notificaciones.models.FormatoANotificacion;
import co.edu.unicauca.notificaciones.repository.FormatoANotificacionRepository;
import co.edu.unicauca.notificaciones.dto.FormatoASubidoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FormatoAConsumerService {

    @Autowired
    private FormatoANotificacionRepository repo;

    @RabbitListener(queues = "${app.rabbitmq.queue.formatoA.subido}")
    public void onMessage(FormatoASubidoEvent e) {
        // Mapear DTO -> Entidad
        FormatoANotificacion notif = new FormatoANotificacion();
        notif.setIdProyecto(e.getIdProyecto());
        notif.setTitulo(e.getTitulo());
        notif.setCoordinadorEmail(e.getCoordinadorEmail());

        repo.save(notif);

        log.info(
                "\n=== NOTIFICACIÓN FORMATO A ===\n" +
                        "Para: {}\n" +
                        "Asunto: Nuevo Formato A recibido\n" +
                        "Proyecto: {} (ID: {})\n" +
                        "Mensaje: Por favor revise el Formato A del proyecto.\n",
                e.getCoordinadorEmail(), e.getTitulo(), e.getIdProyecto()
        );
    }
}
