package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.*;

public class FormatoAAprobadoState implements EstadoProyecto {
    @Override public void evaluar(ProyectoGrado p, boolean a, String o) {
        throw new IllegalStateException("Formato A ya aprobado.");
    }
    @Override public void reintentar(ProyectoGrado p) {
        throw new IllegalStateException("Proyecto aprobado. No aplica reintento.");
    }
    @Override public String getNombreEstado() {
        return "FORMATO_A_APROBADO";
    }
}
