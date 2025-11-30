package co.edu.unicauca.proyectos.models;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.edu.unicauca.proyectos.models.estados.*;

@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
public class ProyectoGrado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String modalidad;                  // INVESTIGACION | PRACTICA_PROFESIONAL
    private String directorEmail;
    private String codirectorEmail;
    private String estudiante1Email;
    private String estudiante2Email;

    private String evaluador1Email;
    private String evaluador2Email;

    @Column(length = 2000)
    private String objetivoGeneral;

    @Column(length = 4000)
    private String objetivosEspecificos;

    // Persistente: alinea con columna existente en BD
    @Column(name = "NUMERO_INTENTO", nullable = false)
    private Integer numeroIntento;             // 0..3

    private String estadoActual = "EN_PRIMERA_EVALUACION_FORMATO_A";

    @Transient
    private EstadoProyecto estado = new EnPrimeraEvaluacionState();

    @Column(length = 2000)
    private String observacionesEvaluacion;

    @Column(columnDefinition = "date")
    private LocalDate fechaFormatoA;

    @Column(columnDefinition = "date")
    private LocalDate fechaAnteproyecto;

    private String formatoAToken;
    private String cartaToken;
    private String anteproyectoToken;

    // Defaults para columnas NOT NULL
    @PrePersist
    void prePersist() {
        if (numeroIntento == null) numeroIntento = 0;
        if (estadoActual == null || estadoActual.isBlank())
            estadoActual = "EN_PRIMERA_EVALUACION_FORMATO_A";
        if (estado == null) estado = fromNombre(estadoActual);
    }

    // Estados
    public void setEstado(EstadoProyecto nuevo) {
        this.estado = nuevo;
        this.estadoActual = nuevo.getNombreEstado();
    }

    public String getEstadoActual() { return estadoActual; }

    public void evaluar(boolean aprobado, String observaciones) {
        if (estado == null) setEstado(fromNombre(estadoActual));
        this.estado.evaluar(this, aprobado, observaciones);
    }

    public void reintentar() {
        if (estado == null) setEstado(fromNombre(estadoActual));
        this.estado.reintentar(this);
    }

    private EstadoProyecto fromNombre(String nombre) {
        return switch (nombre) {
            case "EN_PRIMERA_EVALUACION_FORMATO_A" -> new EnPrimeraEvaluacionState();
            case "EN_SEGUNDA_EVALUACION_FORMATO_A" -> new EnSegundaEvaluacionState();
            case "EN_TERCERA_EVALUACION_FORMATO_A" -> new EnTerceraEvaluacionState();
            case "FORMATO_A_APROBADO" -> new FormatoAAprobadoState();
            case "RECHAZADO_DEFINITIVO" -> new RechazadoDefinitivoState();
            default -> new EnPrimeraEvaluacionState();
        };
    }

    // Helpers usados por los estados
    public void incrementarIntentoORechazarDefinitivo() {
        if (numeroIntento == null) numeroIntento = 0;
        if (numeroIntento >= 2) {
            setEstado(new RechazadoDefinitivoState());
        } else {
            numeroIntento += 1;
        }
    }

    // Adaptadores de compatibilidad
    public Integer getNumeroIntento() { return this.numeroIntento; }
    public void setNumeroIntento(int n) { this.numeroIntento = n; }

    // Si en algún código se usa get/setIntentos, mantén estos proxies:
    public Integer getIntentos() { return this.numeroIntento; }
    public void setIntentos(Integer n) { this.numeroIntento = n; }
}
