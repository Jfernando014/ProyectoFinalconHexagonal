package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.models.EstadoProyecto;
import org.springframework.stereotype.Component;

@Component
public class FormatoARechazadoState implements EstadoProyecto {
    @Override
    public void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        throw new IllegalStateException("El proyecto ya fue rechazado. Debe reintentar primero.");
    }

    @Override
    public void reintentar(ProyectoGrado proyecto) {
        int nuevoIntento = proyecto.getNumeroIntento() + 1;
        if (nuevoIntento >= 3) {
            proyecto.setEstado(new RechazadoDefinitivoState());
        } else if (nuevoIntento == 2) {
            proyecto.setEstado(new EnSegundaEvaluacionState());
        } else if (nuevoIntento == 3) {
            proyecto.setEstado(new EnTerceraEvaluacionState());
        }
        proyecto.setNumeroIntento(nuevoIntento);
    }

    @Override
    public String getNombreEstado() {
        return "FORMATO_A_RECHAZADO";
    }
}