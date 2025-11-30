package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.*;

public class RechazadoDefinitivoState implements EstadoProyecto {
    @Override public void evaluar(ProyectoGrado p, boolean a, String o) {
        throw new IllegalStateException("Rechazado definitivo.");
    }
    @Override public void reintentar(ProyectoGrado p) {
        throw new IllegalStateException("No hay más reintentos. Rechazo definitivo.");
    }
    @Override public String getNombreEstado() {
        return "RECHAZADO_DEFINITIVO";
    }
}
