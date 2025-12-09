package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.*;

public class EnSegundaEvaluacionState implements EstadoProyecto {

    @Override
    public void evaluar(ProyectoGrado p, boolean aprobado, String obs) {
        p.setObservacionesEvaluacion(obs);

        if (aprobado) {
            p.setEstado(new FormatoAAprobadoState());
        } else {
            // Incrementa el intento
            p.incrementarIntentoORechazarDefinitivo();

            // Si aún no es rechazo definitivo, pasa a estado de corrección
            if (!"RECHAZADO_DEFINITIVO".equals(p.getEstadoActual())) {
                p.setEstado(new EnCorreccionFormatoAState());
            }
        }
    }

    @Override
    public void reintentar(ProyectoGrado p) {
        // Cuando el estudiante reenvía, vuelve a quedar listo para evaluación
        p.setEstado(new EnSegundaEvaluacionState());
    }

    @Override
    public String getNombreEstado() {
        return "EN_SEGUNDA_EVALUACION_FORMATO_A";
    }
}
