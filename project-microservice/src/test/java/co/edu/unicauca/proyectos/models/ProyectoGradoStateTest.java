package co.edu.unicauca.proyectos.models;

import co.edu.unicauca.proyectos.models.estados.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProyectoGradoStateTest {

    @Test
    void aprobacionEnPrimera_pasaAAprobado_yBloqueaReintento() {
        ProyectoGrado p = new ProyectoGrado();
        p.setEstado(new EnPrimeraEvaluacionState());

        p.getEstado().evaluar(p, true, "ok");

        assertEquals("FORMATO_A_APROBADO", p.getEstadoActual());
        assertThrows(IllegalStateException.class, () -> p.getEstado().evaluar(p, true, "otra"));
    }

    @Test
    void tresRechazos_quedaRechazadoDefinitivo() {
        ProyectoGrado p = new ProyectoGrado();
        p.setEstado(new EnPrimeraEvaluacionState());

        p.getEstado().evaluar(p, false, "rechazo1");
        assertEquals("EN_SEGUNDA_EVALUACION_FORMATO_A", p.getEstadoActual());

        p.getEstado().evaluar(p, false, "rechazo2");
        assertEquals("EN_TERCERA_EVALUACION_FORMATO_A", p.getEstadoActual());

        p.getEstado().evaluar(p, false, "rechazo3");
        assertEquals("RECHAZADO_DEFINITIVO", p.getEstadoActual());
    }

    @Test
    void reintentoEnPrimera_noPermitido() {
        ProyectoGrado p = new ProyectoGrado();
        p.setEstado(new EnPrimeraEvaluacionState());

        assertThrows(IllegalStateException.class, () -> p.getEstado().reintentar(p));
    }
}
