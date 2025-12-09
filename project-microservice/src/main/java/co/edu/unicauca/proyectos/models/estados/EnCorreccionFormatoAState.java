package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.EstadoProyecto;
import co.edu.unicauca.proyectos.models.ProyectoGrado;

public class EnCorreccionFormatoAState implements EstadoProyecto {
    @Override
    public void evaluar(ProyectoGrado p, boolean aprobado, String obs) {
        throw new IllegalStateException("En corrección, no se puede evaluar.");
    }

    @Override
    public void reintentar(ProyectoGrado p) {
        int intento = p.getNumeroIntento();

        if (intento == 1) {
            p.setEstado(new EnSegundaEvaluacionState());
        } else if (intento == 2) {
            p.setEstado(new EnTerceraEvaluacionState());
        }
    }

    @Override
    public String getNombreEstado() {
        return "EN_CORRECCION_FORMATO_A";
    }
}
