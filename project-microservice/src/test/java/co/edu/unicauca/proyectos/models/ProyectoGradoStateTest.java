package co.edu.unicauca.proyectos.models;

import co.edu.unicauca.proyectos.models.estados.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Patrón State para Proyectos de Grado")
class ProyectoGradoStateTest {

    private ProyectoGrado proyecto;

    @BeforeEach
    void setUp() {
        proyecto = new ProyectoGrado();
    }

    // ========== PRUEBAS DEL ESTADO INICIAL ==========

    @Test
    @DisplayName("Proyecto se inicializa correctamente")
    void proyectoSeInicializaCorrectamente() {
        assertEquals("EN_PRIMERA_EVALUACION_FORMATO_A", proyecto.getEstadoActual());
        assertNotNull(proyecto.getNumeroIntento());
        assertEquals(0, proyecto.getNumeroIntento());
    }
    // ========== PRUEBAS PRIMERA EVALUACIÓN ==========

    @Nested
    @DisplayName("Primera Evaluación")
    class PrimeraEvaluacionTests {

        @Test
        @DisplayName("Aprobación en primera evaluación pasa a APROBADO")
        void aprobacionEnPrimera_pasaAAprobado() {
            proyecto.setEstado(new EnPrimeraEvaluacionState());

            proyecto.getEstado().evaluar(proyecto, true, "Excelente trabajo");

            assertEquals("FORMATO_A_APROBADO", proyecto.getEstadoActual());
            assertEquals("Excelente trabajo", proyecto.getObservacionesEvaluacion());
        }

        @Test
        @DisplayName("Rechazo en primera evaluación pasa a corrección y aumenta intento")
        void rechazoEnPrimera_pasaACorreccion() {
            proyecto.setEstado(new EnPrimeraEvaluacionState());
            proyecto.setNumeroIntento(0);

            proyecto.getEstado().evaluar(proyecto, false, "Faltan detalles");

            assertEquals("EN_CORRECCION_FORMATO_A", proyecto.getEstadoActual());
            assertEquals(1, proyecto.getNumeroIntento());
            assertEquals("Faltan detalles", proyecto.getObservacionesEvaluacion());
        }

        @Test
        @DisplayName("Reintento en primera evaluación no permitido")
        void reintentoEnPrimera_noPermitido() {
            proyecto.setEstado(new EnPrimeraEvaluacionState());

            assertThrows(IllegalStateException.class,
                    () -> proyecto.getEstado().reintentar(proyecto),
                    "No aplica reintento en primera evaluación.");
        }

        @Test
        @DisplayName("No se puede evaluar nuevamente un proyecto aprobado")
        void noSePuedeEvaluarProyectoAprobado() {
            proyecto.setEstado(new FormatoAAprobadoState());

            assertThrows(IllegalStateException.class,
                    () -> proyecto.getEstado().evaluar(proyecto, true, "intento"),
                    "Formato A ya aprobado.");
        }
    }

    // ========== PRUEBAS SEGUNDA EVALUACIÓN ==========

    @Nested
    @DisplayName("Segunda Evaluación")
    class SegundaEvaluacionTests {

        @BeforeEach
        void setUp() {
            proyecto.setEstado(new EnSegundaEvaluacionState());
            proyecto.setNumeroIntento(1);
        }

        @Test
        @DisplayName("Aprobación en segunda evaluación pasa a APROBADO")
        void aprobacionEnSegunda_pasaAAprobado() {
            proyecto.getEstado().evaluar(proyecto, true, "Corregido correctamente");

            assertEquals("FORMATO_A_APROBADO", proyecto.getEstadoActual());
            assertEquals("Corregido correctamente", proyecto.getObservacionesEvaluacion());
        }

        @Test
        @DisplayName("Rechazo en segunda evaluación pasa a corrección (intento 2)")
        void rechazoEnSegunda_pasaACorreccion() {
            proyecto.getEstado().evaluar(proyecto, false, "Aún hay errores");

            assertEquals("EN_CORRECCION_FORMATO_A", proyecto.getEstadoActual());
            assertEquals(2, proyecto.getNumeroIntento());
        }

        @Test
        @DisplayName("Reintento en segunda evaluación reinicia estado")
        void reintentoEnSegunda_reiniciaEstado() {
            proyecto.setObservacionesEvaluacion("Observaciones previas");

            proyecto.getEstado().reintentar(proyecto);

            assertEquals("EN_SEGUNDA_EVALUACION_FORMATO_A", proyecto.getEstadoActual());
            assertEquals(1, proyecto.getNumeroIntento()); // Mantiene el intento
        }
    }

    // ========== PRUEBAS TERCERA EVALUACIÓN ==========

    @Nested
    @DisplayName("Tercera Evaluación")
    class TerceraEvaluacionTests {

        @BeforeEach
        void setUp() {
            proyecto.setEstado(new EnTerceraEvaluacionState());
            proyecto.setNumeroIntento(2);
        }

        @Test
        @DisplayName("Aprobación en tercera evaluación pasa a APROBADO")
        void aprobacionEnTercera_pasaAAprobado() {
            proyecto.getEstado().evaluar(proyecto, true, "Última oportunidad aprobada");

            assertEquals("FORMATO_A_APROBADO", proyecto.getEstadoActual());
        }

        @Test
        @DisplayName("Rechazo en tercera evaluación pasa a RECHAZADO DEFINITIVO")
        void rechazoEnTercera_pasaARechazadoDefinitivo() {
            proyecto.getEstado().evaluar(proyecto, false, "No cumple requisitos mínimos");

            assertEquals("RECHAZADO_DEFINITIVO", proyecto.getEstadoActual());
        }

        @Test
        @DisplayName("No se puede reintentar en tercera evaluación")
        void reintentoEnTercera_noPermitido() {
            assertThrows(IllegalStateException.class,
                    () -> proyecto.getEstado().reintentar(proyecto),
                    "No hay más reintentos.");
        }
    }

    // ========== PRUEBAS ESTADO CORRECCIÓN ==========

    @Nested
    @DisplayName("Estado de Corrección")
    class CorreccionTests {

        @Test
        @DisplayName("No se puede evaluar en estado de corrección")
        void noSePuedeEvaluarEnCorreccion() {
            proyecto.setEstado(new EnCorreccionFormatoAState());

            assertThrows(IllegalStateException.class,
                    () -> proyecto.getEstado().evaluar(proyecto, true, "intento"),
                    "En corrección, no se puede evaluar.");
        }

        @Test
        @DisplayName("Reintento en corrección con intento 1 pasa a segunda evaluación")
        void reintentoEnCorreccion_intento1_pasaASegunda() {
            proyecto.setEstado(new EnCorreccionFormatoAState());
            proyecto.setNumeroIntento(1);

            proyecto.getEstado().reintentar(proyecto);

            assertEquals("EN_SEGUNDA_EVALUACION_FORMATO_A", proyecto.getEstadoActual());
        }

        @Test
        @DisplayName("Reintento en corrección con intento 2 pasa a tercera evaluación")
        void reintentoEnCorreccion_intento2_pasaATercera() {
            proyecto.setEstado(new EnCorreccionFormatoAState());
            proyecto.setNumeroIntento(2);

            proyecto.getEstado().reintentar(proyecto);

            assertEquals("EN_TERCERA_EVALUACION_FORMATO_A", proyecto.getEstadoActual());
        }
    }

    // ========== PRUEBAS RECHAZADO DEFINITIVO ==========

    @Nested
    @DisplayName("Rechazado Definitivo")
    class RechazadoDefinitivoTests {

        @BeforeEach
        void setUp() {
            proyecto.setEstado(new RechazadoDefinitivoState());
        }

        @Test
        @DisplayName("No se puede evaluar proyecto rechazado definitivamente")
        void noSePuedeEvaluarRechazadoDefinitivo() {
            assertThrows(IllegalStateException.class,
                    () -> proyecto.getEstado().evaluar(proyecto, true, "intento"),
                    "Rechazado definitivo.");
        }

        @Test
        @DisplayName("No se puede reintentar proyecto rechazado definitivamente")
        void noSePuedeReintentarRechazadoDefinitivo() {
            assertThrows(IllegalStateException.class,
                    () -> proyecto.getEstado().reintentar(proyecto),
                    "No hay más reintentos. Rechazo definitivo.");
        }
    }

    // ========== PRUEBAS APROBADO ==========

    @Nested
    @DisplayName("Formato A Aprobado")
    class FormatoAAprobadoTests {

        @BeforeEach
        void setUp() {
            proyecto.setEstado(new FormatoAAprobadoState());
        }

        @Test
        @DisplayName("No se puede evaluar proyecto aprobado")
        void noSePuedeEvaluarProyectoAprobado() {
            assertThrows(IllegalStateException.class,
                    () -> proyecto.getEstado().evaluar(proyecto, false, "intento"),
                    "Formato A ya aprobado.");
        }

        @Test
        @DisplayName("No se puede reintentar proyecto aprobado")
        void noSePuedeReintentarProyectoAprobado() {
            assertThrows(IllegalStateException.class,
                    () -> proyecto.getEstado().reintentar(proyecto),
                    "Proyecto aprobado. No aplica reintento.");
        }
    }

    // ========== PRUEBAS FLUJO COMPLETO ==========

    @Nested
    @DisplayName("Flujos Completos")
    class FlujosCompletosTests {

        @Test
        @DisplayName("Flujo: Rechazo → Corrección → Reintento → Aprobación")
        void flujoRechazoCorreccionReintentoAprobacion() {
            // Estado inicial
            proyecto.setEstado(new EnPrimeraEvaluacionState());
            proyecto.setNumeroIntento(0);

            // 1. Rechazo en primera
            proyecto.getEstado().evaluar(proyecto, false, "Corregir sección 2");
            assertEquals("EN_CORRECCION_FORMATO_A", proyecto.getEstadoActual());
            assertEquals(1, proyecto.getNumeroIntento());

            // 2. Reintento (estudiante envía correcciones)
            proyecto.getEstado().reintentar(proyecto);
            assertEquals("EN_SEGUNDA_EVALUACION_FORMATO_A", proyecto.getEstadoActual());
            assertEquals(1, proyecto.getNumeroIntento());

            // 3. Aprobación en segunda
            proyecto.getEstado().evaluar(proyecto, true, "Corregido satisfactoriamente");
            assertEquals("FORMATO_A_APROBADO", proyecto.getEstadoActual());
        }

        @Test
        @DisplayName("Flujo: Tres rechazos consecutivos → Rechazo definitivo")
        void flujoTresRechazos_quedaRechazadoDefinitivo() {
            proyecto.setEstado(new EnPrimeraEvaluacionState());
            proyecto.setNumeroIntento(0);

            // 1er rechazo
            proyecto.getEstado().evaluar(proyecto, false, "Rechazo 1");
            proyecto.getEstado().reintentar(proyecto); // De corrección a segunda

            // 2do rechazo
            proyecto.getEstado().evaluar(proyecto, false, "Rechazo 2");
            proyecto.getEstado().reintentar(proyecto); // De corrección a tercera

            // 3er rechazo → RECHAZADO DEFINITIVO
            proyecto.getEstado().evaluar(proyecto, false, "Rechazo 3");

            assertEquals("RECHAZADO_DEFINITIVO", proyecto.getEstadoActual());
        }

        @Test
        @DisplayName("Flujo: Aprobación directa en primera evaluación")
        void flujoAprobacionDirecta() {
            proyecto.setEstado(new EnPrimeraEvaluacionState());
            proyecto.setNumeroIntento(0);

            proyecto.getEstado().evaluar(proyecto, true, "Excelente desde el inicio");

            assertEquals("FORMATO_A_APROBADO", proyecto.getEstadoActual());
            assertEquals(0, proyecto.getNumeroIntento()); // No aumentó intentos
        }
    }

    // ========== PRUEBAS LÓGICA INTERNA ==========

    @Test
    @DisplayName("Método incrementarIntentoORechazarDefinitivo funciona correctamente")
    void incrementarIntentoORechazarDefinitivo() {
        ProyectoGrado proyecto = new ProyectoGrado();

        // Intento 0 → 1
        proyecto.setNumeroIntento(0);
        proyecto.incrementarIntentoORechazarDefinitivo();
        assertEquals(1, proyecto.getNumeroIntento());

        // Intento 1 → 2
        proyecto.setNumeroIntento(1);
        proyecto.incrementarIntentoORechazarDefinitivo();
        assertEquals(2, proyecto.getNumeroIntento());

        // Intento 2 → 3 y cambia estado a RECHAZADO DEFINITIVO
        proyecto.setNumeroIntento(2);
        proyecto.incrementarIntentoORechazarDefinitivo();
        assertEquals("RECHAZADO_DEFINITIVO", proyecto.getEstadoActual());
    }

    @Test
    @DisplayName("Método fromNombre convierte correctamente strings a estados")
    void testFromNombre() {
        ProyectoGrado proyecto = new ProyectoGrado();

        // Método privado, probamos indirectamente
        proyecto.setEstadoActual("EN_PRIMERA_EVALUACION_FORMATO_A");
        proyecto.evaluar(false, "test");
        assertTrue(proyecto.getEstadoActual().contains("CORRECCION") ||
                proyecto.getEstadoActual().contains("SEGUNDA"));
    }

    // ========== PRUEBAS DE VALIDACIÓN ==========

    @Test
    @DisplayName("Observaciones se guardan correctamente en evaluación")
    void observacionesSeGuardanCorrectamente() {
        proyecto.setEstado(new EnPrimeraEvaluacionState());

        String observaciones = "Debe mejorar la justificación, agregar más referencias bibliográficas y corregir formato.";
        proyecto.getEstado().evaluar(proyecto, false, observaciones);

        assertEquals(observaciones, proyecto.getObservacionesEvaluacion());
    }

    @Test
    @DisplayName("Estado se mantiene consistente entre estado y estadoActual")
    void estadoConsistenteEntreCampos() {
        proyecto.setEstado(new EnSegundaEvaluacionState());

        assertEquals("EN_SEGUNDA_EVALUACION_FORMATO_A", proyecto.getEstadoActual());
        assertEquals("EN_SEGUNDA_EVALUACION_FORMATO_A", proyecto.getEstado().getNombreEstado());
    }
}