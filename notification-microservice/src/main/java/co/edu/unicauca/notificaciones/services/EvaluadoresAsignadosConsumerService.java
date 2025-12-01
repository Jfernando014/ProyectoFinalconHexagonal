package co.edu.unicauca.notificaciones.services;

import co.edu.unicauca.notificaciones.dto.AsignacionEvaluadoresEvent;
import co.edu.unicauca.notificaciones.models.EvaluadoresAsignadosNotificacion;
import co.edu.unicauca.notificaciones.repository.EvaluadoresAsignadosNotificacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EvaluadoresAsignadosConsumerService {

    @Autowired
    private EvaluadoresAsignadosNotificacionRepository repo;

    @RabbitListener(queues = "${app.rabbitmq.queue.evaluadores.asignados}")
    public void consumirAsignacion(AsignacionEvaluadoresEvent e) {

        EvaluadoresAsignadosNotificacion notif = new EvaluadoresAsignadosNotificacion();
        notif.setIdProyecto(e.getIdProyecto());
        notif.setTitulo(e.getTitulo());
        notif.setJefeDepartamentoEmail(e.getJefeDepartamentoEmail());
        notif.setEvaluador1Email(e.getEvaluador1Email());
        notif.setEvaluador2Email(e.getEvaluador2Email());
        notif.setEstudianteEmail1(e.getEstudianteEmail1());
        notif.setEstudianteEmail2(e.getEstudianteEmail2());
        notif.setDirectorEmail(e.getDirectorEmail());
        notif.setCodirectorEmail(e.getCodirectorEmail());

        repo.save(notif);

        // Simulación de envío de email (logger)
        log.info(
                "\n=== NOTIFICACIÓN ASIGNACIÓN DE EVALUADORES ===\n" +
                        "Proyecto: {} (ID: {})\n" +
                        "Jefe de departamento: {}\n" +
                        "Evaluadores: {}, {}\n" +
                        "Estudiantes: {}, {}\n" +
                        "Director: {}\n" +
                        "Codirector: {}\n",
                e.getTitulo(), e.getIdProyecto(),
                e.getJefeDepartamentoEmail(),
                e.getEvaluador1Email(), e.getEvaluador2Email(),
                e.getEstudianteEmail1(), e.getEstudianteEmail2(),
                e.getDirectorEmail(),
                e.getCodirectorEmail()
        );
    }
}
