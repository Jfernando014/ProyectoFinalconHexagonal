package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.*;

public class EnSegundaEvaluacionState implements EstadoProyecto {
    @Override
    public void evaluar(ProyectoGrado p, boolean aprobado, String obs) {
        p.setObservacionesEvaluacion(obs);
        if (aprobado) {
            p.setEstado(new FormatoAAprobadoState());
        } else {
            p.incrementarIntentoORechazarDefinitivo();     // intentos=2
            if (!"RECHAZADO_DEFINITIVO".equals(p.getEstadoActual()))
                p.setEstado(new EnTerceraEvaluacionState());
        }
    }
    @Override public void reintentar(ProyectoGrado p) {
        // permitir nueva versión post-rechazo de 1ª
        p.setEstado(new EnSegundaEvaluacionState());
    }
    @Override public String getNombreEstado() {
        return "EN_SEGUNDA_EVALUACION_FORMATO_A";
    }
}
