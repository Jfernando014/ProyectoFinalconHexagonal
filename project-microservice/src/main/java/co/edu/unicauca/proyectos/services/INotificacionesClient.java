package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.dto.FormatoASubidoEvent;
import co.edu.unicauca.proyectos.dto.AnteproyectoSubidoEvent;
import co.edu.unicauca.proyectos.dto.EvaluacionFormatoAEvent;
import co.edu.unicauca.proyectos.dto.AsignacionEvaluadoresEvent;

/**
 * Puerto de salida (hexagonal) para notificaciones.
 * La capa de aplicación lo usa sin conocer detalles de RabbitMQ.
 */
public interface INotificacionesClient {

    void notificarFormatoASubido(FormatoASubidoEvent event);

    void notificarEvaluacionFormatoA(EvaluacionFormatoAEvent event);

    void notificarAnteproyectoSubido(AnteproyectoSubidoEvent event);

    void notificarAsignacionEvaluadores(AsignacionEvaluadoresEvent event);
}
