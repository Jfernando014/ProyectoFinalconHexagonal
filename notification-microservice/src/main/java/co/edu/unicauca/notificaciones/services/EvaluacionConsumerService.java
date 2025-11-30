package co.edu.unicauca.notificaciones.services;

import co.edu.unicauca.notificaciones.models.EvaluacionNotificacion;
import co.edu.unicauca.notificaciones.repository.EvaluacionNotificacionRepository;
import co.edu.unicauca.notificaciones.dto.EvaluacionFormatoAEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EvaluacionConsumerService {

    @Autowired
    private EvaluacionNotificacionRepository repo;

    @RabbitListener(queues = "${app.rabbitmq.queue.formatoA.evaluado}")
    public void onMessage(EvaluacionFormatoAEvent e) {
        // Mapear DTO -> Entidad
        EvaluacionNotificacion notif = new EvaluacionNotificacion();
        notif.setIdProyecto(e.getIdProyecto());
        notif.setAprobado(e.isAprobado());
        notif.setObservaciones(e.getObservaciones());
        // Si tu entidad tiene un arreglo/lista de destinatarios, constrúyelo:
        // p.ej. estudiante1, estudiante2, director, codirector (evita nulls)
        // Ajusta según tu modelo:
        // notif.setDestinatarios(buildDestinatarios(e));

        repo.save(notif);

        String resultado = e.isAprobado() ? "APROBADO" : "RECHAZADO";
        log.info(
                "=== NOTIFICACIÓN DE EVALUACIÓN ===\n" +
                        "Asunto: Evaluación de Formato A - {}\n" +
                        "Proyecto ID: {}\n" +
                        "Observaciones: {}\n" +
                        "Est(s): {},{} | Dir: {} | Co-dir: {}",
                resultado,
                e.getIdProyecto(),
                e.getObservaciones() != null ? e.getObservaciones() : "Ninguna",
                e.getEstudianteEmail1(), e.getEstudianteEmail2(),
                e.getDirectorEmail(), e.getCodirectorEmail()
        );
    }
}
