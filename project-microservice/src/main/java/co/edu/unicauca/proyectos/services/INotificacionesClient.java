package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.dto.FormatoASubidoEvent;
import co.edu.unicauca.proyectos.dto.ProyectoEvaluadoEvent;
import co.edu.unicauca.proyectos.dto.AnteproyectoSubidoEvent;

public interface INotificacionesClient {
    void notificarFormatoASubido(FormatoASubidoEvent event);
    void notificarEvaluacion(ProyectoEvaluadoEvent event);
    void notificarAnteproyectoSubido(AnteproyectoSubidoEvent event);
}
