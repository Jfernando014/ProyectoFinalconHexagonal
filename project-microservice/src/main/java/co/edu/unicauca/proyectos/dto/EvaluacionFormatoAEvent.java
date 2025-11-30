package co.edu.unicauca.proyectos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Debe coincidir con lo que consume notification-microservice:
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionFormatoAEvent {
    private Long idProyecto;
    private String titulo;
    private boolean aprobado;
    private String observaciones;

    private String estudianteEmail1;
    private String estudianteEmail2;
    private String directorEmail;
    private String codirectorEmail;
    private String coordinadorEmail;
}
