package co.edu.unicauca.proyectos.services.evaluacion;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluadoresTest {

    @Test
    void evaluadorAprobacionSeteaEstadoYAObservaciones() {
        ProyectoGrado p = new ProyectoGrado();
        EvaluadorAprobacion ev = new EvaluadorAprobacion();

        ev.evaluarProyecto(p, true, "aprobado");

        assertEquals("FORMATO_A_APROBADO", p.getEstadoActual());
        assertEquals("aprobado", p.getObservacionesEvaluacion());
    }

    @Test
    void evaluadorRechazoSeteaEstadoYAObservaciones() {
        ProyectoGrado p = new ProyectoGrado();
        EvaluadorRechazo ev = new EvaluadorRechazo();

        ev.evaluarProyecto(p, false, "rechazado");

        assertEquals("FORMATO_A_RECHAZADO", p.getEstadoActual());
        assertEquals("rechazado", p.getObservacionesEvaluacion());
    }
}
