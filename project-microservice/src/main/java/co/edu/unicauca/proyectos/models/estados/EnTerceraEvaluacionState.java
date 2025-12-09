package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.*;

public class EnTerceraEvaluacionState implements EstadoProyecto {

    @Override
    public void evaluar(ProyectoGrado p, boolean aprobado, String obs) {
        p.setObservacionesEvaluacion(obs);

        if (aprobado) {
            p.setEstado(new FormatoAAprobadoState());
        } else {
            // Último intento
            p.incrementarIntentoORechazarDefinitivo();

            // Si aún no está marcado como definitivo, pasa a corrección
            if (!"RECHAZADO_DEFINITIVO".equals(p.getEstadoActual())) {
                p.setEstado(new EnCorreccionFormatoAState());
            }
        }
    }

    @Override
    public void reintentar(ProyectoGrado p) {
        throw new IllegalStateException("No hay más reintentos.");
    }

    @Override
    public String getNombreEstado() {
        return "EN_TERCERA_EVALUACION_FORMATO_A";
    }
}
