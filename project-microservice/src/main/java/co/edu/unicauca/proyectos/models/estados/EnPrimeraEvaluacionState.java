package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.*;

public class EnPrimeraEvaluacionState implements EstadoProyecto {
    @Override
    public void evaluar(ProyectoGrado p, boolean aprobado, String obs) {
        p.setObservacionesEvaluacion(obs);
        if (aprobado) {
            p.setEstado(new FormatoAAprobadoState());
        } else {
            p.incrementarIntentoORechazarDefinitivo();     // intentos=1
            if (!"RECHAZADO_DEFINITIVO".equals(p.getEstadoActual()))
                p.setEstado(new EnSegundaEvaluacionState());
        }
    }
    @Override public void reintentar(ProyectoGrado p) {
        throw new IllegalStateException("No aplica reintento en primera evaluación.");
    }
    @Override public String getNombreEstado() {
        return "EN_PRIMERA_EVALUACION_FORMATO_A";
    }
}
