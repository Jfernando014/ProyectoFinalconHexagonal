package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.*;

public class EnPrimeraEvaluacionState implements EstadoProyecto {

    @Override
    public void evaluar(ProyectoGrado p, boolean aprobado, String obs) {
        p.setObservacionesEvaluacion(obs);

        if (aprobado) {
            p.setEstado(new FormatoAAprobadoState());
        } else {
            // Aumenta el intento
            p.incrementarIntentoORechazarDefinitivo();

            // Si no fue rechazo definitivo, pasa a estado de CORRECCIÓN
            if (!"RECHAZADO_DEFINITIVO".equals(p.getEstadoActual())) {
                p.setEstado(new EnCorreccionFormatoAState());
            }
        }
    }

    @Override
    public void reintentar(ProyectoGrado p) {
        throw new IllegalStateException("No aplica reintento en primera evaluación.");
    }

    @Override
    public String getNombreEstado() {
        return "EN_PRIMERA_EVALUACION_FORMATO_A";
    }
}
